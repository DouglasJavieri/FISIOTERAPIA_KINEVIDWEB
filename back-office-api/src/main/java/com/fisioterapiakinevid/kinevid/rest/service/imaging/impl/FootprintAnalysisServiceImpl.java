package com.fisioterapiakinevid.kinevid.rest.service.imaging.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootprintAnalysisRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootprintAnalysisResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.FootAnalysis;
import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.FootprintAnalysis;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.SessionStatus;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.FootprintType;
import com.fisioterapiakinevid.kinevid.rest.repository.imaging.FootAnalysisRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.imaging.FootprintAnalysisRepository;
import com.fisioterapiakinevid.kinevid.rest.service.imaging.FootprintAnalysisService;
import com.fisioterapiakinevid.kinevid.rest.util.FormatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de Valoración de Hernández Corvo.
 * Lógica upsert: crear, actualizar o reactivar según el estado del registro.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FootprintAnalysisServiceImpl implements FootprintAnalysisService {

    private final FootprintAnalysisRepository footprintRepository;
    private final FootAnalysisRepository footAnalysisRepository;

    @Override
    @Transactional
    public FootprintAnalysisResponseDto saveOrUpdate(Long footAnalysisId, FootprintAnalysisRequestDto request) throws OperationException {
        try {
            FootAnalysis footAnalysis = findFootAnalysisById(footAnalysisId);
            validateSessionIsOpen(footAnalysis);

            FootprintType footprintType = parseFootprintType(request.getFootprintType());

            // Buscar registro existente incluyendo eliminados para upsert
            var existing = footprintRepository.findByFootAnalysisIdIncludingDeleted(footAnalysisId);

            FootprintAnalysis entity;
            if (existing.isPresent()) {
                entity = existing.get();
                if (entity.isDeleted()) {
                    entity.setDeleted(false);
                    log.info("Valoración Hernández Corvo reactivada — análisis ID={}", footAnalysisId);
                }
            } else {
                entity = FootprintAnalysis.builder()
                        .footAnalysis(footAnalysis)
                        .build();
            }

            entity.setFootprintType(footprintType);
            entity.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);

            footprintRepository.save(entity);
            log.info("Valoración Hernández Corvo guardada — ID={}, análisis ID={}, tipo={}",
                    entity.getId(), footAnalysisId, footprintType);

            return mapToResponseDto(entity);

        } catch (OperationException e) {
            log.error("Error al guardar valoración Hernández Corvo (análisis ID={}): {}", footAnalysisId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al guardar valoración Hernández Corvo (análisis ID={})", footAnalysisId, e);
            throw new OperationException("Ocurrió un error inesperado al guardar la valoración de huella plantar");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FootprintAnalysisResponseDto getByFootAnalysisId(Long footAnalysisId) throws OperationException {
        try {
            findFootAnalysisById(footAnalysisId);
            FootprintAnalysis entity = footprintRepository.findByFootAnalysisId(footAnalysisId)
                    .orElseThrow(() -> new OperationException(
                            "No se encontró una valoración de huella plantar para el análisis con ID '" + footAnalysisId + "'."));
            return mapToResponseDto(entity);
        } catch (OperationException e) {
            log.error("Error al obtener valoración Hernández Corvo (análisis ID={}): {}", footAnalysisId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al obtener valoración Hernández Corvo (análisis ID={})", footAnalysisId, e);
            throw new OperationException("Ocurrió un error inesperado al obtener la valoración de huella plantar");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FootprintAnalysisResponseDto getById(Long id) throws OperationException {
        try {
            return mapToResponseDto(findByIdActive(id));
        } catch (OperationException e) {
            log.error("Error al obtener valoración Hernández Corvo ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al obtener valoración Hernández Corvo ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al obtener la valoración de huella plantar");
        }
    }

    @Override
    @Transactional
    public void delete(Long id) throws OperationException {
        try {
            FootprintAnalysis entity = findByIdWithRelations(id);
            validateSessionIsOpen(entity.getFootAnalysis());

            entity.setDeleted(true);
            footprintRepository.save(entity);
            log.info("Valoración Hernández Corvo ID={} eliminada lógicamente", id);

        } catch (OperationException e) {
            log.error("Error al eliminar valoración Hernández Corvo ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al eliminar valoración Hernández Corvo ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al eliminar la valoración de huella plantar");
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

    private FootprintAnalysis findByIdActive(Long id) throws OperationException {
        FootprintAnalysis entity = footprintRepository.findById(id)
                .orElseThrow(() -> new OperationException(
                        FormatUtil.noRegistrado("Valoración de Huella Plantar", id)));
        if (entity.isDeleted()) {
            throw new OperationException(FormatUtil.noRegistrado("Valoración de Huella Plantar", id));
        }
        return entity;
    }

    private FootprintAnalysis findByIdWithRelations(Long id) throws OperationException {
        return footprintRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new OperationException(
                        FormatUtil.noRegistrado("Valoración de Huella Plantar", id)));
    }

    private void validateSessionIsOpen(FootAnalysis footAnalysis) throws OperationException {
        if (footAnalysis.getClinicalSession() == null) {
            throw new OperationException("El análisis de pisada no tiene una sesión clínica asociada.");
        }
        if (footAnalysis.getClinicalSession().getSessionStatus() != SessionStatus.OPEN) {
            throw new OperationException(
                    "Solo se puede gestionar la valoración de huella plantar en sesiones con estado ABIERTA. " +
                    "Estado actual: " + footAnalysis.getClinicalSession().getSessionStatus().getDescription());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PARSEO DE ENUM
    // ─────────────────────────────────────────────────────────────────────────

    private FootprintType parseFootprintType(String value) throws OperationException {
        try {
            return FootprintType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperationException(
                    "Tipo de huella plantar no válido: '" + value + "'. " +
                    "Valores permitidos: INDEX_NORMAL, INDEX_FLAT_FOOT, INDEX_CAVUS_FOOT, " +
                    "FLAT_FOOT, FLAT_NORMAL, NORMAL, NORMAL_CAVUS, CAVUS_FOOT, STRONG_CAVUS, EXTREME_CAVUS");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  MAPEO — Entidad → ResponseDto
    // ─────────────────────────────────────────────────────────────────────────

    private FootprintAnalysisResponseDto mapToResponseDto(FootprintAnalysis fp) {
        return FootprintAnalysisResponseDto.builder()
                .id(fp.getId())
                .footAnalysisId(fp.getFootAnalysis() != null ? fp.getFootAnalysis().getId() : null)
                .footprintType(fp.getFootprintType() != null ? fp.getFootprintType().name() : null)
                .footprintTypeDescription(fp.getFootprintType() != null ? fp.getFootprintType().getDescription() : null)
                .notes(fp.getNotes())
                .build();
    }
}
