package com.fisioterapiakinevid.kinevid.rest.service.clinical;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.SessionServiceRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.SessionServiceResponseDTO;

import java.util.List;

/**
 * Servicio para gestionar los servicios médicos aplicados en una sesión clínica (N:M).
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 24/04/2026
 */
public interface SessionServiceManagementService {

    /**
     * Agrega un servicio médico a una sesión. Si ya existe, actualiza cantidad y notas.
     */
    SessionServiceResponseDTO addServiceToSession(Long sessionId, SessionServiceRequestDTO request) throws OperationException;

    /**
     * Retorna todos los servicios aplicados en una sesión (no eliminados).
     */
    List<SessionServiceResponseDTO> getServicesBySession(Long sessionId) throws OperationException;

    /**
     * Elimina lógicamente un servicio de una sesión.
     */
    void removeServiceFromSession(Long sessionServiceId) throws OperationException;
}

