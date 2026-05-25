package com.fisioterapiakinevid.kinevid.rest.service.imaging.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootAnalysisRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootAnalysisResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootAnalysisUpdateRequestDTO;
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

        return dto;
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
