package com.fisioterapiakinevid.kinevid.rest.service.clinical;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalEpisodeRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalEpisodeResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.CloseEpisodeRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.EpisodeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
public interface ClinicalEpisodeService {

    ClinicalEpisodeResponseDTO createEpisode(ClinicalEpisodeRequestDTO request) throws OperationException;

    ClinicalEpisodeResponseDTO getEpisodeById(Long id) throws OperationException;

    Page<ClinicalEpisodeResponseDTO> getEpisodesByPatientId(Long patientId, Pageable pageable) throws OperationException;

    Page<ClinicalEpisodeResponseDTO> getAllEpisodes(Long patientId, EpisodeStatus status, Pageable pageable) throws OperationException;

    ClinicalEpisodeResponseDTO closeEpisode(Long id, CloseEpisodeRequestDTO request) throws OperationException;

    ClinicalEpisodeResponseDTO reactivatePatient(Long patientId, ClinicalEpisodeRequestDTO request) throws OperationException;
}



