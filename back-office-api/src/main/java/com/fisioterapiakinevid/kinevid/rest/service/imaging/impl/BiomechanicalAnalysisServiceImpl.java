package com.fisioterapiakinevid.kinevid.rest.service.imaging.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.BiomechanicalAnalysisRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.BiomechanicalAnalysisResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.BiomechanicalAnalysis;
import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.FootAnalysis;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.SessionStatus;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.FootSide;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.GaitType;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.TibialMalleolarRule;
import com.fisioterapiakinevid.kinevid.rest.repository.imaging.BiomechanicalAnalysisRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.imaging.FootAnalysisRepository;
import com.fisioterapiakinevid.kinevid.rest.service.imaging.BiomechanicalAnalysisService;
import com.fisioterapiakinevid.kinevid.rest.util.FormatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de análisis biomecánico por pie.
 * Lógica upsert: crear, actualizar o reactivar según el estado del registro.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BiomechanicalAnalysisServiceImpl implements BiomechanicalAnalysisService {

    private final BiomechanicalAnalysisRepository biomechanicalRepository;
    private final FootAnalysisRepository footAnalysisRepository;

    @Override
    @Transactional
    public BiomechanicalAnalysisResponseDto saveOrUpdate(Long footAnalysisId, BiomechanicalAnalysisRequestDto request) throws OperationException {
        try {
            FootAnalysis footAnalysis = findFootAnalysisById(footAnalysisId);
            validateSessionIsOpen(footAnalysis);

            FootSide footSide = parseFootSide(request.getFootSide());

            // Buscar registro existente (incluye eliminados para reactivar)
            var existing = biomechanicalRepository
                    .findByFootAnalysisIdAndFootSideIncludingDeleted(footAnalysisId, footSide);

            BiomechanicalAnalysis entity;
            if (existing.isPresent()) {
                entity = existing.get();
                // Reactivar si fue eliminado lógicamente
                if (entity.isDeleted()) {
                    entity.setDeleted(false);
                    log.info("Análisis biomecánico reactivado — análisis ID={}, pie={}", footAnalysisId, footSide);
                }
            } else {
                entity = BiomechanicalAnalysis.builder()
                        .footAnalysis(footAnalysis)
                        .footSide(footSide)
                        .build();
            }

            // Actualizar campos clínicos
            applyFields(entity, request);

            biomechanicalRepository.save(entity);
            log.info("Análisis biomecánico guardado — ID={}, análisis ID={}, pie={}",
                    entity.getId(), footAnalysisId, footSide);

            return mapToResponseDto(entity);

        } catch (OperationException e) {
            log.error("Error al guardar análisis biomecánico (análisis ID={}, pie={}): {}",
                    footAnalysisId, request.getFootSide(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al guardar análisis biomecánico (análisis ID={})", footAnalysisId, e);
            throw new OperationException("Ocurrió un error inesperado al guardar el análisis biomecánico");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BiomechanicalAnalysisResponseDto> getByFootAnalysisId(Long footAnalysisId) throws OperationException {
        try {
            findFootAnalysisById(footAnalysisId);
            return biomechanicalRepository.findAllByFootAnalysisId(footAnalysisId)
                    .stream()
                    .map(this::mapToResponseDto)
                    .toList();
        } catch (OperationException e) {
            log.error("Error al listar análisis biomecánicos del análisis ID={}: {}", footAnalysisId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al listar análisis biomecánicos del análisis ID={}", footAnalysisId, e);
            throw new OperationException("Ocurrió un error inesperado al listar los análisis biomecánicos");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BiomechanicalAnalysisResponseDto getById(Long id) throws OperationException {
        try {
            return mapToResponseDto(findByIdActive(id));
        } catch (OperationException e) {
            log.error("Error al obtener análisis biomecánico ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al obtener análisis biomecánico ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al obtener el análisis biomecánico");
        }
    }

    @Override
    @Transactional
    public void delete(Long id) throws OperationException {
        try {
            BiomechanicalAnalysis entity = findByIdWithRelations(id);
            validateSessionIsOpen(entity.getFootAnalysis());

            entity.setDeleted(true);
            biomechanicalRepository.save(entity);
            log.info("Análisis biomecánico ID={} eliminado lógicamente", id);

        } catch (OperationException e) {
            log.error("Error al eliminar análisis biomecánico ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al eliminar análisis biomecánico ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al eliminar el análisis biomecánico");
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

    private BiomechanicalAnalysis findByIdActive(Long id) throws OperationException {
        BiomechanicalAnalysis entity = biomechanicalRepository.findById(id)
                .orElseThrow(() -> new OperationException(
                        FormatUtil.noRegistrado("Análisis Biomecánico", id)));
        if (entity.isDeleted()) {
            throw new OperationException(FormatUtil.noRegistrado("Análisis Biomecánico", id));
        }
        return entity;
    }

    private BiomechanicalAnalysis findByIdWithRelations(Long id) throws OperationException {
        return biomechanicalRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new OperationException(
                        FormatUtil.noRegistrado("Análisis Biomecánico", id)));
    }

    private void validateSessionIsOpen(FootAnalysis footAnalysis) throws OperationException {
        if (footAnalysis.getClinicalSession() == null) {
            throw new OperationException("El análisis de pisada no tiene una sesión clínica asociada.");
        }
        if (footAnalysis.getClinicalSession().getSessionStatus() != SessionStatus.OPEN) {
            throw new OperationException(
                    "Solo se puede gestionar el análisis biomecánico en sesiones con estado ABIERTA. " +
                    "Estado actual: " + footAnalysis.getClinicalSession().getSessionStatus().getDescription());
        }
    }

    private void applyFields(BiomechanicalAnalysis entity, BiomechanicalAnalysisRequestDto request) throws OperationException {
        if (request.getTibialMalleolarRule() != null) {
            entity.setTibialMalleolarRule(parseTibialMalleolarRule(request.getTibialMalleolarRule()));
        }
        if (request.getShoeWear() != null) {
            entity.setShoeWear(request.getShoeWear().trim());
        }
        if (request.getTibiaPalpation() != null) {
            entity.setTibiaPalpation(request.getTibiaPalpation().trim());
        }
        if (request.getGait() != null) {
            entity.setGait(parseGaitType(request.getGait()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PARSEO DE ENUMS
    // ─────────────────────────────────────────────────────────────────────────

    private FootSide parseFootSide(String value) throws OperationException {
        try {
            return FootSide.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperationException(
                    "Lado del pie no válido: '" + value + "'. Valores permitidos: LEFT, RIGHT");
        }
    }

    private TibialMalleolarRule parseTibialMalleolarRule(String value) throws OperationException {
        try {
            return TibialMalleolarRule.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperationException(
                    "Regla maleolo tibial no válida: '" + value + "'. Valores permitidos: NORMAL, VARUS, VALGUS, OTHER");
        }
    }

    private GaitType parseGaitType(String value) throws OperationException {
        try {
            return GaitType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperationException(
                    "Tipo de marcha no válido: '" + value + "'. Valores permitidos: NORMAL, PRONATOR, SUPINATOR, MIXED");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  MAPEO — Entidad → ResponseDto
    // ─────────────────────────────────────────────────────────────────────────

    private BiomechanicalAnalysisResponseDto mapToResponseDto(BiomechanicalAnalysis ba) {
        return BiomechanicalAnalysisResponseDto.builder()
                .id(ba.getId())
                .footAnalysisId(ba.getFootAnalysis() != null ? ba.getFootAnalysis().getId() : null)
                .footSide(ba.getFootSide() != null ? ba.getFootSide().name() : null)
                .footSideDescription(ba.getFootSide() != null ? ba.getFootSide().getDescription() : null)
                .tibialMalleolarRule(ba.getTibialMalleolarRule() != null ? ba.getTibialMalleolarRule().name() : null)
                .tibialMalleolarRuleDescription(ba.getTibialMalleolarRule() != null ? ba.getTibialMalleolarRule().getDescription() : null)
                .shoeWear(ba.getShoeWear())
                .tibiaPalpation(ba.getTibiaPalpation())
                .gait(ba.getGait() != null ? ba.getGait().name() : null)
                .gaitDescription(ba.getGait() != null ? ba.getGait().getDescription() : null)
                .build();
    }
}
