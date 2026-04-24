package com.fisioterapiakinevid.kinevid.rest.service.clinical;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalSessionRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalSessionResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalSessionUpdateRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
public interface ClinicalSessionService {

    ClinicalSessionResponseDTO createSession(ClinicalSessionRequestDTO request) throws OperationException;

    ClinicalSessionResponseDTO getSessionById(Long id) throws OperationException;

    Page<ClinicalSessionResponseDTO> getSessionsByEpisodeId(Long episodeId, SessionStatus status, Pageable pageable) throws OperationException;

    ClinicalSessionResponseDTO updateSession(Long id, ClinicalSessionUpdateRequestDTO request) throws OperationException;

    ClinicalSessionResponseDTO changeSessionStatus(Long id, SessionStatus status) throws OperationException;

    void deleteSession(Long id) throws OperationException;
}

