package com.fisioterapiakinevid.kinevid.rest.service.clinical.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalSessionRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalSessionResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalSessionUpdateRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.ClinicalEpisode;
import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.ClinicalSession;
import com.fisioterapiakinevid.kinevid.rest.model.entity.emp.Employee;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.EpisodeStatus;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.SessionStatus;
import com.fisioterapiakinevid.kinevid.rest.repository.clinical.ClinicalSessionRepository;
import com.fisioterapiakinevid.kinevid.rest.service.clinical.ClinicalEpisodeService;
import com.fisioterapiakinevid.kinevid.rest.service.clinical.ClinicalSessionService;
import com.fisioterapiakinevid.kinevid.rest.service.emp.EmployeeService;
import com.fisioterapiakinevid.kinevid.rest.util.FormatUtil;
import com.fisioterapiakinevid.kinevid.rest.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * @author Douglas Cristhian Javieri Vino
 * Creado: 23/04/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ClinicalSessionServiceImpl implements ClinicalSessionService {

    private final ClinicalSessionRepository sessionRepository;
    private final ClinicalEpisodeService episodeService;
    private final EmployeeService employeeService;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public ClinicalSessionResponseDTO createSession(ClinicalSessionRequestDTO request) throws OperationException {
        try {
            // Validar episodio y que esté ACTIVO
            var episodeDTO = episodeService.getEpisodeById(request.getEpisodeId());
            if (episodeDTO.getEpisodeStatus() != EpisodeStatus.ACTIVE) {
                throw new OperationException(
                        "No se puede crear una sesión en un episodio con estado: " + episodeDTO.getEpisodeStatus().getDescription() +
                        ". El episodio debe estar ACTIVO.");
            }

            // Validar fisioterapeuta
            employeeService.getEmployeeById(request.getEmployeeId());

            ValidationUtil.throwExceptionIfInvalidText("Motivo de consulta", request.getReasonForConsultation(), true, 500);

            // Calcular número de sesión
            int nextSessionNumber = sessionRepository
                    .findMaxSessionNumberByEpisodeId(request.getEpisodeId())
                    .map(max -> max + 1)
                    .orElse(1);

            // Referencias JPA reales para que Hibernate pueda cargar relaciones lazy luego
            ClinicalEpisode episodeRef = entityManager.getReference(ClinicalEpisode.class, request.getEpisodeId());
            Employee employeeRef = entityManager.getReference(Employee.class, request.getEmployeeId());

            ClinicalSession session = ClinicalSession.builder()
                    .episode(episodeRef)
                    .employee(employeeRef)
                    .sessionDate(request.getSessionDate() != null ? request.getSessionDate() : LocalDate.now())
                    .sessionNumber(nextSessionNumber)
                    .reasonForConsultation(request.getReasonForConsultation().trim())
                    .relevantBackground(request.getRelevantBackground() != null ? request.getRelevantBackground().trim() : null)
                    .hasImageAnalysis(false)
                    .sessionStatus(SessionStatus.OPEN)
                    .build();

            sessionRepository.save(session);
            sessionRepository.flush(); // Asegurar que el ID se genere antes de continuar
            Long sessionId = session.getId();
            log.info("Sesión {} creada para episodio ID={} - SessionID={}", nextSessionNumber, request.getEpisodeId(), sessionId);

            // Validar que el ID fue asignado
            if (sessionId == null) {
                log.error("ERROR CRÍTICO: sessionId es null después del flush. Session Object: {}", session);
                throw new OperationException("Error al generar el ID de la sesión. Por favor, intente nuevamente.");
            }

            // Recargar con relaciones para armar el DTO completo
            ClinicalSession loadedSession = loadSessionWithRelations(sessionId);
            return new ClinicalSessionResponseDTO(loadedSession);

        } catch (OperationException e) {
            log.error("Error al crear sesión: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear sesión", e);
            throw new OperationException("Ocurrió un error inesperado al crear la sesión clínica");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ClinicalSessionResponseDTO getSessionById(Long id) throws OperationException {
        try {
            return new ClinicalSessionResponseDTO(loadSessionWithRelations(id));
        } catch (OperationException e) {
            log.error("Error al obtener sesión ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al obtener sesión ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al obtener la sesión clínica");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClinicalSessionResponseDTO> getSessionsByEpisodeId(Long episodeId, SessionStatus status, Pageable pageable) throws OperationException {
        try {
            episodeService.getEpisodeById(episodeId);
            if (status != null) {
                return sessionRepository.findAllByEpisodeIdAndStatus(episodeId, status, pageable)
                        .map(ClinicalSessionResponseDTO::new);
            }
            return sessionRepository.findAllByEpisodeId(episodeId, pageable)
                    .map(ClinicalSessionResponseDTO::new);
        } catch (OperationException e) {
            log.error("Error al listar sesiones del episodio ID={}: {}", episodeId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al listar sesiones del episodio ID={}", episodeId, e);
            throw new OperationException("Ocurrió un error inesperado al listar las sesiones clínicas");
        }
    }

    @Override
    @Transactional
    public ClinicalSessionResponseDTO updateSession(Long id, ClinicalSessionUpdateRequestDTO request) throws OperationException {
        try {
            ClinicalSession session = findSessionById(id);

            if (session.getSessionStatus() != SessionStatus.OPEN) {
                throw new OperationException(
                        "Solo se pueden editar sesiones en estado ABIERTA. Estado actual: " +
                        session.getSessionStatus().getDescription());
            }

            // Actualizar fisioterapeuta si se envía
            if (request.getEmployeeId() != null) {
                employeeService.getEmployeeById(request.getEmployeeId()); // valida existencia
                session.setEmployee(entityManager.getReference(Employee.class, request.getEmployeeId()));
            }

            if (request.getReasonForConsultation() != null && !request.getReasonForConsultation().isBlank()) {
                ValidationUtil.throwExceptionIfInvalidText("Motivo de consulta", request.getReasonForConsultation(), false, 500);
                session.setReasonForConsultation(request.getReasonForConsultation().trim());
            }
            if (request.getRelevantBackground() != null) {
                session.setRelevantBackground(request.getRelevantBackground().trim());
            }
            if (request.getKinesiologicalEvaluation() != null) {
                session.setKinesiologicalEvaluation(request.getKinesiologicalEvaluation().trim());
            }
            if (request.getActualIllnessHistory() != null) {
                session.setActualIllnessHistory(request.getActualIllnessHistory().trim());
            }
            if (request.getGait() != null) {
                session.setGait(request.getGait().trim());
            }
            if (request.getFunctionalTests() != null) {
                session.setFunctionalTests(request.getFunctionalTests().trim());
            }
            if (request.getComplementaryExams() != null) {
                session.setComplementaryExams(request.getComplementaryExams().trim());
            }
            if (request.getKinesiologicalDiagnosis() != null) {
                session.setKinesiologicalDiagnosis(request.getKinesiologicalDiagnosis().trim());
            }
            if (request.getTreatmentApplied() != null) {
                session.setTreatmentApplied(request.getTreatmentApplied().trim());
            }
            if (request.getObservations() != null) {
                session.setObservations(request.getObservations().trim());
            }
            if (request.getEvolution() != null) {
                session.setEvolution(request.getEvolution().trim());
            }

            sessionRepository.save(session);
            log.info("Sesión ID={} actualizada", id);
            return new ClinicalSessionResponseDTO(loadSessionWithRelations(session.getId()));

        } catch (OperationException e) {
            log.error("Error al actualizar sesión ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al actualizar sesión ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al actualizar la sesión clínica");
        }
    }

    @Override
    @Transactional
    public ClinicalSessionResponseDTO changeSessionStatus(Long id, SessionStatus status) throws OperationException {
        try {
            ClinicalSession session = findSessionById(id);

            if (session.getSessionStatus() == status) {
                throw new OperationException("La sesión ya se encuentra en el estado '" + status.getDescription() + "'.");
            }
            // No se puede reabrir una sesión cerrada
            if (session.getSessionStatus() == SessionStatus.CLOSED && status == SessionStatus.OPEN) {
                throw new OperationException("No se puede reabrir una sesión ya CERRADA.");
            }

            session.setSessionStatus(status);
            sessionRepository.save(session);
            log.info("Estado de sesión ID={} cambiado a: {}", id, status);
            return new ClinicalSessionResponseDTO(loadSessionWithRelations(session.getId()));

        } catch (OperationException e) {
            log.error("Error al cambiar estado de sesión ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al cambiar estado de sesión ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al cambiar el estado de la sesión");
        }
    }

    @Override
    @Transactional
    public void deleteSession(Long id) throws OperationException {
        try {
            ClinicalSession session = findSessionById(id);
            if (session.getSessionStatus() == SessionStatus.CLOSED) {
                throw new OperationException("No se puede eliminar una sesión CERRADA.");
            }
            session.setDeleted(true);
            sessionRepository.save(session);
            log.info("Sesión ID={} eliminada lógicamente", id);
        } catch (OperationException e) {
            log.error("Error al eliminar sesión ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al eliminar sesión ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al eliminar la sesión clínica");
        }
    }


    private ClinicalSession findSessionById(Long id) throws OperationException {
        ClinicalSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Sesión Clínica", id)));
        if (session.isDeleted()) {
            throw new OperationException(FormatUtil.noRegistrado("Sesión Clínica", id));
        }
        return session;
    }


    private ClinicalSession loadSessionWithRelations(Long sessionId) throws OperationException {
        return sessionRepository.findByIdWithRelations(sessionId)
                .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Sesión Clínica", sessionId)));
    }
}

