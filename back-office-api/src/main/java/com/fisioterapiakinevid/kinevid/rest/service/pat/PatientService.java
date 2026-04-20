package com.fisioterapiakinevid.kinevid.rest.service.pat;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.pat.PatientRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.pat.PatientResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.pat.PatientUpdateRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.enums.pat.PatientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
public interface PatientService {

    PatientResponseDTO createPatient(PatientRequestDTO request) throws OperationException;

    PatientResponseDTO getPatientById(Long id) throws OperationException;

    Page<PatientResponseDTO> getAllPatients(Pageable pageable, PatientStatus status, String search) throws OperationException;

    PatientResponseDTO updatePatient(Long id, PatientUpdateRequestDTO request) throws OperationException;

    PatientResponseDTO changePatientStatus(Long id, PatientStatus status) throws OperationException;

    void deletePatient(Long id) throws OperationException;

    List<PatientResponseDTO> getActivePatientsList() throws OperationException;
}


