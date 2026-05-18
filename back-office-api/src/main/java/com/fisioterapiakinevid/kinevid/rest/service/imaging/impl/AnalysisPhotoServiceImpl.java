package com.fisioterapiakinevid.kinevid.rest.service.imaging.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.AnalysisPhotoResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.AnnotationsUpdateRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.AnalysisPhoto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.FootAnalysis;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.SessionStatus;
import com.fisioterapiakinevid.kinevid.rest.repository.imaging.AnalysisPhotoRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.imaging.FootAnalysisRepository;
import com.fisioterapiakinevid.kinevid.rest.service.imaging.AnalysisPhotoService;
import com.fisioterapiakinevid.kinevid.rest.service.imaging.StorageResult;
import com.fisioterapiakinevid.kinevid.rest.service.imaging.StorageService;
import com.fisioterapiakinevid.kinevid.rest.util.FormatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Implementación del servicio de fotos del análisis de pisada.
 * Gestiona la subida a Cloudinary vía {@link StorageService} y
 * la persistencia en BD de URL, ID de archivo y anotaciones.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisPhotoServiceImpl implements AnalysisPhotoService {

    private static final int MAX_PHOTOS_PER_ANALYSIS = 6;
    private static final int MIN_PHOTO_ORDER = 1;
    private static final int MAX_PHOTO_ORDER = 6;

    private final AnalysisPhotoRepository photoRepository;
    private final FootAnalysisRepository footAnalysisRepository;
    private final StorageService storageService;

    @Override
    @Transactional
    public AnalysisPhotoResponseDto uploadPhoto(Long footAnalysisId, MultipartFile file, Integer photoOrder) throws OperationException {
        try {
            // Validar que el análisis existe y su sesión está OPEN
            FootAnalysis footAnalysis = findFootAnalysisById(footAnalysisId);
            validateSessionIsOpen(footAnalysis);

            // Validar que el archivo no está vacío
            if (file == null || file.isEmpty()) {
                throw new OperationException("El archivo de imagen no puede estar vacío.");
            }

            // Validar rango de orden (1-6)
            validatePhotoOrder(photoOrder);

            // Validar límite de fotos
            long currentCount = photoRepository.countByFootAnalysisIdAndDeletedFalse(footAnalysisId);
            if (currentCount >= MAX_PHOTOS_PER_ANALYSIS) {
                throw new OperationException(
                        "El análisis ya tiene el máximo de " + MAX_PHOTOS_PER_ANALYSIS + " fotos permitidas.");
            }

            // Validar que el número de orden no está ocupado
            if (photoRepository.existsByFootAnalysisIdAndPhotoOrderAndDeletedFalse(footAnalysisId, photoOrder)) {
                throw new OperationException(
                        "Ya existe una foto en la posición " + photoOrder + " para este análisis. " +
                        "Elimine la foto existente antes de subir una nueva en esa posición.");
            }

            // Subir imagen al proveedor de almacenamiento
            String folderPath = buildFolderPath(footAnalysis);
            StorageResult storageResult = storageService.upload(file, folderPath);

            // Persistir registro en BD
            AnalysisPhoto photo = AnalysisPhoto.builder()
                    .footAnalysis(footAnalysis)
                    .photoOrder(photoOrder)
                    .photoUrl(storageResult.getUrl())
                    .storageFileId(storageResult.getFileId())
                    .annotationsJson(null)
                    .isSelected(false)
                    .build();

            photoRepository.save(photo);
            log.info("Foto subida — análisis ID={}, orden={}, fileId={}",
                    footAnalysisId, photoOrder, storageResult.getFileId());

            return mapToResponseDto(photo);

        } catch (OperationException e) {
            log.error("Error al subir foto al análisis ID={}: {}", footAnalysisId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al subir foto al análisis ID={}", footAnalysisId, e);
            throw new OperationException("Ocurrió un error inesperado al subir la foto");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalysisPhotoResponseDto> getPhotosByAnalysisId(Long footAnalysisId) throws OperationException {
        try {
            findFootAnalysisById(footAnalysisId);
            return photoRepository.findAllByFootAnalysisId(footAnalysisId)
                    .stream()
                    .map(this::mapToResponseDto)
                    .toList();
        } catch (OperationException e) {
            log.error("Error al listar fotos del análisis ID={}: {}", footAnalysisId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al listar fotos del análisis ID={}", footAnalysisId, e);
            throw new OperationException("Ocurrió un error inesperado al listar las fotos del análisis");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AnalysisPhotoResponseDto getPhotoById(Long id) throws OperationException {
        try {
            return mapToResponseDto(findPhotoById(id));
        } catch (OperationException e) {
            log.error("Error al obtener foto ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al obtener foto ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al obtener la foto");
        }
    }

    @Override
    @Transactional
    public AnalysisPhotoResponseDto updateAnnotations(Long id, AnnotationsUpdateRequestDto request) throws OperationException {
        try {
            AnalysisPhoto photo = findPhotoByIdWithRelations(id);
            validateSessionIsOpen(photo.getFootAnalysis());

            photo.setAnnotationsJson(request.getAnnotationsJson().trim());
            photoRepository.save(photo);

            log.info("Anotaciones actualizadas — foto ID={}", id);
            return mapToResponseDto(photo);

        } catch (OperationException e) {
            log.error("Error al actualizar anotaciones de foto ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al actualizar anotaciones de foto ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al guardar las anotaciones");
        }
    }

    @Override
    @Transactional
    public AnalysisPhotoResponseDto toggleSelection(Long id) throws OperationException {
        try {
            AnalysisPhoto photo = findPhotoByIdWithRelations(id);
            validateSessionIsOpen(photo.getFootAnalysis());

            photo.setIsSelected(!photo.getIsSelected());
            photoRepository.save(photo);

            log.info("Selección alternada — foto ID={}, isSelected={}", id, photo.getIsSelected());
            return mapToResponseDto(photo);

        } catch (OperationException e) {
            log.error("Error al alternar selección de foto ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al alternar selección de foto ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al cambiar la selección de la foto");
        }
    }

    @Override
    @Transactional
    public void deletePhoto(Long id) throws OperationException {
        try {
            AnalysisPhoto photo = findPhotoByIdWithRelations(id);
            validateSessionIsOpen(photo.getFootAnalysis());

            // Eliminar del proveedor de almacenamiento primero
            storageService.delete(photo.getStorageFileId());

            // Eliminación lógica en BD
            photo.setDeleted(true);
            photoRepository.save(photo);

            log.info("Foto eliminada — ID={}, fileId={}", id, photo.getStorageFileId());

        } catch (OperationException e) {
            log.error("Error al eliminar foto ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al eliminar foto ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al eliminar la foto");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  MÉTODOS PRIVADOS — Validación y carga
    // ─────────────────────────────────────────────────────────────────────────

    private FootAnalysis findFootAnalysisById(Long footAnalysisId) throws OperationException {
        FootAnalysis fa = footAnalysisRepository.findById(footAnalysisId)
                .orElseThrow(() -> new OperationException(
                        FormatUtil.noRegistrado("Análisis de Pisada", footAnalysisId)));
        if (fa.isDeleted()) {
            throw new OperationException(FormatUtil.noRegistrado("Análisis de Pisada", footAnalysisId));
        }
        return fa;
    }

    private AnalysisPhoto findPhotoById(Long id) throws OperationException {
        AnalysisPhoto photo = photoRepository.findById(id)
                .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Foto de Análisis", id)));
        if (photo.isDeleted()) {
            throw new OperationException(FormatUtil.noRegistrado("Foto de Análisis", id));
        }
        return photo;
    }

    private AnalysisPhoto findPhotoByIdWithRelations(Long id) throws OperationException {
        return photoRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Foto de Análisis", id)));
    }

    private void validateSessionIsOpen(FootAnalysis footAnalysis) throws OperationException {
        if (footAnalysis.getClinicalSession() == null) {
            throw new OperationException("El análisis de pisada no tiene una sesión clínica asociada.");
        }
        if (footAnalysis.getClinicalSession().getSessionStatus() != SessionStatus.OPEN) {
            throw new OperationException(
                    "Solo se pueden gestionar fotos en sesiones con estado ABIERTA. " +
                    "Estado actual: " + footAnalysis.getClinicalSession().getSessionStatus().getDescription());
        }
    }

    private void validatePhotoOrder(Integer photoOrder) throws OperationException {
        if (photoOrder == null) {
            throw new OperationException("El número de posición de la foto es requerido.");
        }
        if (photoOrder < MIN_PHOTO_ORDER || photoOrder > MAX_PHOTO_ORDER) {
            throw new OperationException(
                    "El número de posición debe estar entre " + MIN_PHOTO_ORDER +
                    " y " + MAX_PHOTO_ORDER + ". Valor recibido: " + photoOrder);
        }
    }

    /**
     * Construye la ruta de carpeta en Cloudinary siguiendo la estructura jerárquica:
     * kinevid/patients/{patientId}/episodes/{episodeId}/sessions/{sessionId}/analysis/{analysisId}
     */
    private String buildFolderPath(FootAnalysis footAnalysis) {
        var session = footAnalysis.getClinicalSession();
        var episode = session.getEpisode();
        var patient = episode.getPatient();
        return String.format("kinevid/patients/%d/episodes/%d/sessions/%d/analysis/%d",
                patient.getId(),
                episode.getId(),
                session.getId(),
                footAnalysis.getId());
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  MAPEO — Entidad → ResponseDto (lógica centralizada en ServiceImpl)
    // ─────────────────────────────────────────────────────────────────────────

    private AnalysisPhotoResponseDto mapToResponseDto(AnalysisPhoto photo) {
        return AnalysisPhotoResponseDto.builder()
                .id(photo.getId())
                .footAnalysisId(photo.getFootAnalysis() != null ? photo.getFootAnalysis().getId() : null)
                .photoOrder(photo.getPhotoOrder())
                .photoUrl(photo.getPhotoUrl())
                .storageFileId(photo.getStorageFileId())
                .annotationsJson(photo.getAnnotationsJson())
                .isSelected(photo.getIsSelected())
                .hasAnnotations(photo.getAnnotationsJson() != null && !photo.getAnnotationsJson().isBlank())
                .build();
    }
}
