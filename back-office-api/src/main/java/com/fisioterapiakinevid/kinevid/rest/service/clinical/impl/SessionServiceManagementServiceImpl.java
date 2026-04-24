package com.fisioterapiakinevid.kinevid.rest.service.clinical.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.SessionServiceRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.SessionServiceResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.ClinicalSession;
import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.SessionService;
import com.fisioterapiakinevid.kinevid.rest.model.entity.svc.MedicalService;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.SessionStatus;
import com.fisioterapiakinevid.kinevid.rest.repository.clinical.SessionServiceRepository;
import com.fisioterapiakinevid.kinevid.rest.service.clinical.ClinicalSessionService;
import com.fisioterapiakinevid.kinevid.rest.service.clinical.SessionServiceManagementService;
import com.fisioterapiakinevid.kinevid.rest.service.svc.MedicalServiceService;
import com.fisioterapiakinevid.kinevid.rest.util.FormatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 24/04/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SessionServiceManagementServiceImpl implements SessionServiceManagementService {

    private final SessionServiceRepository sessionServiceRepository;
    private final ClinicalSessionService clinicalSessionService;
    private final MedicalServiceService medicalServiceService;

    @Override
    @Transactional
    public SessionServiceResponseDTO addServiceToSession(Long sessionId, SessionServiceRequestDTO request) throws OperationException {
        try {
            // Validar que la sesión existe y está OPEN
            var sessionDTO = clinicalSessionService.getSessionById(sessionId);
            if (sessionDTO.getSessionStatus() != SessionStatus.OPEN) {
                throw new OperationException(
                        "Solo se pueden agregar servicios a sesiones en estado ABIERTA. Estado actual: "
                        + sessionDTO.getSessionStatus().getDescription());
            }

            // Validar que el servicio médico existe y está activo
            var medicalServiceDTO = medicalServiceService.getServiceById(request.getMedicalServiceId());

            // Si ya existe ese servicio en la sesión → actualizar (upsert)
            var existing = sessionServiceRepository.findBySessionIdAndMedicalServiceId(
                    sessionId, request.getMedicalServiceId());

            SessionService entity;
            if (existing.isPresent()) {
                entity = existing.get();
                entity.setQuantity(request.getQuantity());
                entity.setUnitPrice(request.getUnitPrice());
                entity.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);
                log.info("Servicio ID={} actualizado en sesión ID={}", request.getMedicalServiceId(), sessionId);
            } else {
                // Proxy JPA para las FKs
                ClinicalSession sessionRef = new ClinicalSession();
                sessionRef.setId(sessionId);

                MedicalService serviceRef = new MedicalService();
                serviceRef.setId(request.getMedicalServiceId());

                entity = SessionService.builder()
                        .session(sessionRef)
                        .medicalService(serviceRef)
                        .quantity(request.getQuantity())
                        .unitPrice(request.getUnitPrice())
                        .notes(request.getNotes() != null ? request.getNotes().trim() : null)
                        .build();
                log.info("Servicio ID={} agregado a sesión ID={}", request.getMedicalServiceId(), sessionId);
            }

            sessionServiceRepository.save(entity);

            // Recargar con relaciones para construir el DTO
            return new SessionServiceResponseDTO(
                    sessionServiceRepository.findAllBySessionId(sessionId)
                            .stream()
                            .filter(ss -> ss.getMedicalService().getId().equals(request.getMedicalServiceId()))
                            .findFirst()
                            .orElse(entity)
            );

        } catch (OperationException e) {
            log.error("Error al agregar servicio a sesión ID={}: {}", sessionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al agregar servicio a sesión ID={}", sessionId, e);
            throw new OperationException("Ocurrió un error inesperado al agregar el servicio a la sesión");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionServiceResponseDTO> getServicesBySession(Long sessionId) throws OperationException {
        try {
            clinicalSessionService.getSessionById(sessionId); // valida existencia
            return sessionServiceRepository.findAllBySessionId(sessionId)
                    .stream()
                    .map(SessionServiceResponseDTO::new)
                    .toList();
        } catch (OperationException e) {
            log.error("Error al listar servicios de sesión ID={}: {}", sessionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al listar servicios de sesión ID={}", sessionId, e);
            throw new OperationException("Ocurrió un error inesperado al listar los servicios de la sesión");
        }
    }

    @Override
    @Transactional
    public void removeServiceFromSession(Long sessionServiceId) throws OperationException {
        try {
            SessionService ss = sessionServiceRepository.findById(sessionServiceId)
                    .orElseThrow(() -> new OperationException(
                            FormatUtil.noRegistrado("Servicio de sesión", sessionServiceId)));

            if (ss.isDeleted()) {
                throw new OperationException(FormatUtil.noRegistrado("Servicio de sesión", sessionServiceId));
            }

            // Verificar que la sesión sigue OPEN
            var sessionDTO = clinicalSessionService.getSessionById(ss.getSession().getId());
            if (sessionDTO.getSessionStatus() != SessionStatus.OPEN) {
                throw new OperationException(
                        "No se puede eliminar un servicio de una sesión que no está en estado ABIERTA.");
            }

            ss.setDeleted(true);
            sessionServiceRepository.save(ss);
            log.info("Servicio de sesión ID={} eliminado lógicamente", sessionServiceId);

        } catch (OperationException e) {
            log.error("Error al eliminar servicio de sesión ID={}: {}", sessionServiceId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al eliminar servicio de sesión ID={}", sessionServiceId, e);
            throw new OperationException("Ocurrió un error inesperado al eliminar el servicio de la sesión");
        }
    }
}

