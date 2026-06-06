package com.fisioterapiakinevid.kinevid.rest.service.imaging.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.*;
import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.BiomechanicalAnalysis;
import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.FootprintAnalysis;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.FootSide;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.FootprintType;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.GaitType;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.TibialMalleolarRule;
import com.fisioterapiakinevid.kinevid.rest.repository.imaging.BiomechanicalAnalysisRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.imaging.FootprintAnalysisRepository;
import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.ClinicalSession;
import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.FootAnalysis;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.SessionStatus;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.DiagnosisType;
import com.fisioterapiakinevid.kinevid.rest.repository.clinical.ClinicalSessionRepository;
import com.fisioterapiakinevid.kinevid.rest.repository.imaging.FootAnalysisRepository;
import com.fisioterapiakinevid.kinevid.rest.service.imaging.FootAnalysisService;
import com.fisioterapiakinevid.kinevid.rest.util.FormatUtil;
import com.fisioterapiakinevid.kinevid.rest.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de análisis de pisada.
 * Toda la lógica de mapeo, validación y reglas de negocio se concentra aquí.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FootAnalysisServiceImpl implements FootAnalysisService {

    private final FootAnalysisRepository footAnalysisRepository;
    private final ClinicalSessionRepository clinicalSessionRepository;
    private final BiomechanicalAnalysisRepository biomechanicalRepository;
    private final FootprintAnalysisRepository footprintRepository;

    @Override
    @Transactional
    public FootAnalysisResponseDTO saveFullFootAnalysis(FootAnalysisFullSaveRequestDTO request) throws OperationException {
        try {
            // 1. Gestionar FootAnalysis (Upsert)
            ClinicalSession session = findSessionById(request.getClinicalSessionId());
            validateSessionIsOpen(session);

            Optional<FootAnalysis> existingOpt = footAnalysisRepository.findByClinicalSessionId(session.getId());
            FootAnalysis footAnalysis;

            if (existingOpt.isPresent()) {
                footAnalysis = existingOpt.get();
                log.info("Actualizando FootAnalysis existente ID={} para sesión ID={}", footAnalysis.getId(), session.getId());
            } else {
                footAnalysis = new FootAnalysis();
                footAnalysis.setClinicalSession(session);
                footAnalysis.setAnalysisDate(LocalDateTime.now());
                log.info("Creando nuevo FootAnalysis para sesión ID={}", session.getId());
            }

            // Asegurar que las listas no sean nulas para evitar NullPointerException si se usan antes de persistir
            if (footAnalysis.getBiomechanicalAnalysis() == null) footAnalysis.setBiomechanicalAnalysis(new java.util.LinkedHashSet<>());
            if (footAnalysis.getFootprintAnalysis() == null) footAnalysis.setFootprintAnalysis(new java.util.LinkedHashSet<>());
            if (footAnalysis.getPhotos() == null) footAnalysis.setPhotos(new java.util.LinkedHashSet<>());

            // Aplicar campos de FootAnalysis
            footAnalysis.setDiagnosis(parseDiagnosisType(request.getDiagnosis()));
            footAnalysis.setRelevantBackground(trimOrNull(request.getRelevantBackground()));
            footAnalysis.setKinesiologicalEvaluation(trimOrNull(request.getKinesiologicalEvaluation()));
            footAnalysis.setObservations(trimOrNull(request.getObservations()));
            footAnalysis.setDistanceIntermaleolar(request.getDistanceIntermaleolar());
            footAnalysis.setDistanceIntercondylar(request.getDistanceIntercondylar());
            footAnalysis.setAngleLeftInternal(request.getAngleLeftInternal());
            footAnalysis.setAngleLeftExternal(request.getAngleLeftExternal());
            footAnalysis.setAngleRightInternal(request.getAngleRightInternal());
            footAnalysis.setAngleRightExternal(request.getAngleRightExternal());

            footAnalysisRepository.save(footAnalysis);
            session.setHasImageAnalysis(true);
            clinicalSessionRepository.save(session);

            // 2. Gestionar BiomechanicalAnalysis (Upsert para LEFT y RIGHT)
            saveBiomechanical(footAnalysis, FootSide.LEFT, 
                request.getLeftTibialMalleolarRule(), request.getLeftShoeWear(), 
                request.getLeftTibiaPalpation(), request.getLeftGait());
            
            saveBiomechanical(footAnalysis, FootSide.RIGHT, 
                request.getRightTibialMalleolarRule(), request.getRightShoeWear(), 
                request.getRightTibiaPalpation(), request.getRightGait());

            // 3. Gestionar FootprintAnalysis (Upsert)
            if (request.getFootprintType() != null) {
                FootprintAnalysis footprint = footprintRepository.findByFootAnalysisIdIncludingDeleted(footAnalysis.getId())
                        .orElse(new FootprintAnalysis());
                
                footprint.setFootAnalysis(footAnalysis);
                footprint.setDeleted(false);
                footprint.setFootprintType(parseFootprintType(request.getFootprintType()));
                footprintRepository.save(footprint);
            }

            return mapToResponseDto(loadWithRelations(footAnalysis.getId()));

        } catch (OperationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al guardar análisis integral", e);
            throw new OperationException("Error al procesar el guardado integral del análisis");
        }
    }

    private void saveBiomechanical(FootAnalysis fa, FootSide side, String rule, String wear, String palpation, String gait) throws OperationException {
        BiomechanicalAnalysis bio = biomechanicalRepository.findByFootAnalysisIdAndFootSideIncludingDeleted(fa.getId(), side)
                .orElse(new BiomechanicalAnalysis());
        
        bio.setFootAnalysis(fa);
        bio.setFootSide(side);
        bio.setDeleted(false);
        
        if (rule != null) bio.setTibialMalleolarRule(parseTibialMalleolarRule(rule));
        if (wear != null) bio.setShoeWear(wear.trim());
        if (palpation != null) bio.setTibiaPalpation(palpation.trim());
        if (gait != null) bio.setGait(parseGaitType(gait));
        
        biomechanicalRepository.save(bio);
    }

    private FootprintType parseFootprintType(String value) throws OperationException {
        try {
            return FootprintType.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new OperationException("Tipo de huella no válido: " + value);
        }
    }

    private TibialMalleolarRule parseTibialMalleolarRule(String value) throws OperationException {
        try {
            return TibialMalleolarRule.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new OperationException("Regla maleolo tibial no válida: " + value);
        }
    }

    private GaitType parseGaitType(String value) throws OperationException {
        try {
            return GaitType.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new OperationException("Tipo de marcha no válido: " + value);
        }
    }

    @Override
    @Transactional
    public FootAnalysisResponseDTO createFootAnalysis(FootAnalysisRequestDTO request) throws OperationException {
        try {
            // Validar sesión clínica
            ClinicalSession session = findSessionById(request.getClinicalSessionId());
            validateSessionIsOpen(session);

            // Validar que no exista ya un análisis para esta sesión
            if (footAnalysisRepository.existsByClinicalSessionIdAndDeletedFalse(session.getId())) {
                throw new OperationException("Ya existe un análisis de pisada para la sesión clínica con ID '" + session.getId() + "'.");
            }

            // Validar diagnóstico
            DiagnosisType diagnosisType = parseDiagnosisType(request.getDiagnosis());

            // Construir entidad
            FootAnalysis footAnalysis = FootAnalysis.builder()
                    .clinicalSession(session)
                    .analysisDate(LocalDateTime.now())
                    .relevantBackground(trimOrNull(request.getRelevantBackground()))
                    .kinesiologicalEvaluation(trimOrNull(request.getKinesiologicalEvaluation()))
                    .observations(trimOrNull(request.getObservations()))
                    .diagnosis(diagnosisType)
                    .distanceIntermaleolar(request.getDistanceIntermaleolar())
                    .distanceIntercondylar(request.getDistanceIntercondylar())
                    .angleLeftInternal(request.getAngleLeftInternal())
                    .angleLeftExternal(request.getAngleLeftExternal())
                    .angleRightInternal(request.getAngleRightInternal())
                    .angleRightExternal(request.getAngleRightExternal())
                    .build();

            footAnalysisRepository.save(footAnalysis);

            // Marcar la sesión con análisis de imagen
            session.setHasImageAnalysis(true);
            clinicalSessionRepository.save(session);

            log.info("Análisis de pisada ID={} creado para sesión ID={}", footAnalysis.getId(), session.getId());

            // Recargar con relaciones para respuesta completa
            return mapToResponseDto(loadWithRelations(footAnalysis.getId()));

        } catch (OperationException e) {
            log.error("Error al crear análisis de pisada: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear análisis de pisada", e);
            throw new OperationException("Ocurrió un error inesperado al crear el análisis de pisada");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FootAnalysisResponseDTO getFootAnalysisById(Long id) throws OperationException {
        try {
            return mapToResponseDto(loadWithRelations(id));
        } catch (OperationException e) {
            log.error("Error al obtener análisis de pisada ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al obtener análisis de pisada ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al obtener el análisis de pisada");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FootAnalysisResponseDTO getFootAnalysisBySessionId(Long sessionId) throws OperationException {
        try {
            Optional<FootAnalysis> footAnalysisOpt = footAnalysisRepository.findByClinicalSessionId(sessionId);
            
            if (footAnalysisOpt.isEmpty()) {
                log.info("No se encontró análisis para la sesión ID: {}", sessionId);
                return null;
            }
            
            return mapToResponseDto(footAnalysisOpt.get());
        } catch (Exception e) {
            log.error("Error inesperado al obtener análisis por sesión ID={}", sessionId, e);
            throw new OperationException("Ocurrió un error inesperado al obtener el análisis de pisada");
        }
    }

    @Override
    @Transactional
    public FootAnalysisResponseDTO updateFootAnalysis(Long id, FootAnalysisUpdateRequestDTO request) throws OperationException {
        try {
            FootAnalysis footAnalysis = findFootAnalysisById(id);
            validateSessionIsOpen(footAnalysis.getClinicalSession());

            // Actualizar solo los campos que se envían
            if (request.getDiagnosis() != null) {
                footAnalysis.setDiagnosis(parseDiagnosisType(request.getDiagnosis()));
            }
            if (request.getRelevantBackground() != null) {
                footAnalysis.setRelevantBackground(request.getRelevantBackground().trim());
            }
            if (request.getKinesiologicalEvaluation() != null) {
                footAnalysis.setKinesiologicalEvaluation(request.getKinesiologicalEvaluation().trim());
            }
            if (request.getObservations() != null) {
                footAnalysis.setObservations(request.getObservations().trim());
            }
            if (request.getDistanceIntermaleolar() != null) {
                footAnalysis.setDistanceIntermaleolar(request.getDistanceIntermaleolar());
            }
            if (request.getDistanceIntercondylar() != null) {
                footAnalysis.setDistanceIntercondylar(request.getDistanceIntercondylar());
            }
            if (request.getAngleLeftInternal() != null) {
                footAnalysis.setAngleLeftInternal(request.getAngleLeftInternal());
            }
            if (request.getAngleLeftExternal() != null) {
                footAnalysis.setAngleLeftExternal(request.getAngleLeftExternal());
            }
            if (request.getAngleRightInternal() != null) {
                footAnalysis.setAngleRightInternal(request.getAngleRightInternal());
            }
            if (request.getAngleRightExternal() != null) {
                footAnalysis.setAngleRightExternal(request.getAngleRightExternal());
            }

            footAnalysisRepository.save(footAnalysis);
            log.info("Análisis de pisada ID={} actualizado", id);

            return mapToResponseDto(loadWithRelations(footAnalysis.getId()));

        } catch (OperationException e) {
            log.error("Error al actualizar análisis de pisada ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al actualizar análisis de pisada ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al actualizar el análisis de pisada");
        }
    }

    @Override
    @Transactional
    public void deleteFootAnalysis(Long id) throws OperationException {
        try {
            FootAnalysis footAnalysis = findFootAnalysisById(id);
            validateSessionIsOpen(footAnalysis.getClinicalSession());

            footAnalysis.setDeleted(true);
            footAnalysisRepository.save(footAnalysis);

            // Desmarcar la sesión
            ClinicalSession session = footAnalysis.getClinicalSession();
            session.setHasImageAnalysis(false);
            clinicalSessionRepository.save(session);

            log.info("Análisis de pisada ID={} eliminado lógicamente", id);

        } catch (OperationException e) {
            log.error("Error al eliminar análisis de pisada ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al eliminar análisis de pisada ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al eliminar el análisis de pisada");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  MÉTODOS PRIVADOS — Validación y carga
    // ─────────────────────────────────────────────────────────────────────────

    private ClinicalSession findSessionById(Long sessionId) throws OperationException {
        ClinicalSession session = clinicalSessionRepository.findById(sessionId)
                .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Sesión Clínica", sessionId)));
        if (session.isDeleted()) {
            throw new OperationException(FormatUtil.noRegistrado("Sesión Clínica", sessionId));
        }
        return session;
    }

    private void validateSessionIsOpen(ClinicalSession session) throws OperationException {
        if (session.getSessionStatus() != SessionStatus.OPEN) {
            throw new OperationException(
                    "Solo se puede gestionar el análisis de pisada en sesiones con estado ABIERTA. " +
                    "Estado actual: " + session.getSessionStatus().getDescription());
        }
    }

    private FootAnalysis findFootAnalysisById(Long id) throws OperationException {
        FootAnalysis footAnalysis = footAnalysisRepository.findById(id)
                .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Análisis de Pisada", id)));
        if (footAnalysis.isDeleted()) {
            throw new OperationException(FormatUtil.noRegistrado("Análisis de Pisada", id));
        }
        return footAnalysis;
    }

    private FootAnalysis loadWithRelations(Long id) throws OperationException {
        return footAnalysisRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Análisis de Pisada", id)));
    }

    private DiagnosisType parseDiagnosisType(String diagnosis) throws OperationException {
        try {
            return DiagnosisType.valueOf(diagnosis.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperationException(
                    "Diagnóstico no válido: '" + diagnosis + "'. Valores permitidos: NORMAL, PRONATION, SUPINATION");
        }
    }

    private String trimOrNull(String value) {
        return value != null ? value.trim() : null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  MAPEO — Entidad → ResponseDTO (lógica de mapeo centralizada en servicio)
    // ─────────────────────────────────────────────────────────────────────────

    private FootAnalysisResponseDTO mapToResponseDto(FootAnalysis fa) {
        FootAnalysisResponseDTO dto = new FootAnalysisResponseDTO();

        dto.setId(fa.getId());
        dto.setAnalysisDate(fa.getAnalysisDate());

        // Contexto clínico
        if (fa.getClinicalSession() != null) {
            var session = fa.getClinicalSession();
            dto.setClinicalSessionId(session.getId());
            dto.setSessionNumber(session.getSessionNumber());
            dto.setSessionStatus(session.getSessionStatus() != null
                    ? session.getSessionStatus().getValue()
                    : null);

            if (session.getEpisode() != null) {
                var episode = session.getEpisode();
                dto.setEpisodeId(episode.getId());
                dto.setEpisodeNumber(episode.getEpisodeNumber());

                if (episode.getPatient() != null) {
                    var patient = episode.getPatient();
                    dto.setPatientId(patient.getId());
                    dto.setPatientCi(patient.getCi());
                    dto.setPatientFullName(buildFullName(
                            patient.getFirstName(),
                            patient.getPaternalSurname(),
                            patient.getMaternalSurname()));
                }
            }

            if (session.getEmployee() != null) {
                var emp = session.getEmployee();
                dto.setEmployeeId(emp.getId());
                dto.setEmployeeFullName(buildFullName(
                        emp.getFirstName(),
                        emp.getPaternalSurname(),
                        emp.getMaternalSurname()));
            }
        }

        // Datos del análisis
        dto.setRelevantBackground(fa.getRelevantBackground());
        dto.setKinesiologicalEvaluation(fa.getKinesiologicalEvaluation());
        dto.setObservations(fa.getObservations());
        dto.setDiagnosis(fa.getDiagnosis());

        // Mediciones
        dto.setDistanceIntermaleolar(fa.getDistanceIntermaleolar());
        dto.setDistanceIntercondylar(fa.getDistanceIntercondylar());
        dto.setAngleLeftInternal(fa.getAngleLeftInternal());
        dto.setAngleLeftExternal(fa.getAngleLeftExternal());
        dto.setAngleRightInternal(fa.getAngleRightInternal());
        dto.setAngleRightExternal(fa.getAngleRightExternal());

        // Mapear relaciones para el frontend
        if (fa.getBiomechanicalAnalysis() != null) {
            dto.setBiomechanicalAnalysis(fa.getBiomechanicalAnalysis().stream()
                    .map(this::mapBiomechanicalToDto)
                    .collect(Collectors.toList()));
        }

        if (fa.getFootprintAnalysis() != null) {
            dto.setFootprintAnalysis(fa.getFootprintAnalysis().stream()
                    .map(this::mapFootprintToDto)
                    .collect(Collectors.toList()));
        }

        if (fa.getPhotos() != null) {
            dto.setPhotos(fa.getPhotos().stream()
                    .map(this::mapPhotoToDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private BiomechanicalAnalysisResponseDto mapBiomechanicalToDto(BiomechanicalAnalysis b) {
        return BiomechanicalAnalysisResponseDto.builder()
                .id(b.getId())
                .footAnalysisId(b.getFootAnalysis().getId())
                .footSide(b.getFootSide().name())
                .footSideDescription(b.getFootSide().getDescription())
                .tibialMalleolarRule(b.getTibialMalleolarRule() != null ? b.getTibialMalleolarRule().name() : null)
                .tibialMalleolarRuleDescription(b.getTibialMalleolarRule() != null ? b.getTibialMalleolarRule().getDescription() : null)
                .shoeWear(b.getShoeWear())
                .tibiaPalpation(b.getTibiaPalpation())
                .gait(b.getGait() != null ? b.getGait().name() : null)
                .gaitDescription(b.getGait() != null ? b.getGait().getDescription() : null)
                .build();
    }

    private FootprintAnalysisResponseDto mapFootprintToDto(FootprintAnalysis f) {
        return FootprintAnalysisResponseDto.builder()
                .id(f.getId())
                .footAnalysisId(f.getFootAnalysis().getId())
                .footprintType(f.getFootprintType().name())
                .footprintTypeDescription(f.getFootprintType().getDescription())
                .notes(f.getNotes())
                .build();
    }

    private AnalysisPhotoResponseDto mapPhotoToDto(com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.AnalysisPhoto p) {
        return AnalysisPhotoResponseDto.builder()
                .id(p.getId())
                .footAnalysisId(p.getFootAnalysis().getId())
                .photoOrder(p.getPhotoOrder())
                .photoUrl(p.getPhotoUrl())
                .storageFileId(p.getStorageFileId())
                .annotationsJson(p.getAnnotationsJson())
                .isSelected(p.getIsSelected())
                .hasAnnotations(p.getAnnotationsJson() != null && !p.getAnnotationsJson().isBlank())
                .build();
    }

    private String buildFullName(String firstName, String paternalSurname, String maternalSurname) {
        StringBuilder sb = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) {
            sb.append(firstName);
        }
        if (paternalSurname != null && !paternalSurname.isBlank()) {
            sb.append(" ").append(paternalSurname);
        }
        if (maternalSurname != null && !maternalSurname.isBlank()) {
            sb.append(" ").append(maternalSurname);
        }
        return sb.toString().trim();
    }
}
