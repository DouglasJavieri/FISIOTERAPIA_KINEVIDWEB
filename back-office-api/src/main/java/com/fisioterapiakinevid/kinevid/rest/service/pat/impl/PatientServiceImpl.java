package com.fisioterapiakinevid.kinevid.rest.service.pat.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.pat.PatientRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.pat.PatientResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.pat.PatientUpdateRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.entity.pat.Patient;
import com.fisioterapiakinevid.kinevid.rest.model.enums.pat.PatientStatus;
import com.fisioterapiakinevid.kinevid.rest.repository.pat.PatientRepository;
import com.fisioterapiakinevid.kinevid.rest.service.pat.PatientService;
import com.fisioterapiakinevid.kinevid.rest.util.FormatUtil;
import com.fisioterapiakinevid.kinevid.rest.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public PatientResponseDTO createPatient(PatientRequestDTO request) throws OperationException {
        try {
            ValidationUtil.throwExceptionIfInvalidText("Nombre", request.getFirstName(), true, 80);
            ValidationUtil.throwExceptionIfInvalidText("Apellido paterno", request.getPaternalSurname(), true, 80);
            ValidationUtil.throwExceptionIfInvalidText("Carnet de identidad", request.getCi(), true, 20);

            if (request.getGender() == null) {
                throw new OperationException("El gÃ©nero es requerido");
            }
            if (request.getBirthDate() == null) {
                throw new OperationException("La fecha de nacimiento es requerida");
            }
            if (patientRepository.existsByCi(request.getCi().trim())) {
                throw new OperationException(FormatUtil.yaRegistrado("Paciente", "carnet", request.getCi()));
            }

            Patient patient = Patient.builder()
                    .firstName(request.getFirstName().trim())
                    .paternalSurname(request.getPaternalSurname().trim())
                    .maternalSurname(request.getMaternalSurname() != null ? request.getMaternalSurname().trim() : null)
                    .ci(request.getCi().trim())
                    .gender(request.getGender())
                    .birthDate(request.getBirthDate())
                    .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                    .email(request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null)
                    .address(request.getAddress() != null ? request.getAddress().trim() : null)
                    .bloodType(request.getBloodType())
                    .occupation(request.getOccupation() != null ? request.getOccupation().trim() : null)
                    .emergencyContactName(request.getEmergencyContactName() != null ? request.getEmergencyContactName().trim() : null)
                    .emergencyContactPhone(request.getEmergencyContactPhone() != null ? request.getEmergencyContactPhone().trim() : null)
                    .notes(request.getNotes() != null ? request.getNotes().trim() : null)
                    .status(PatientStatus.ACTIVE)
                    .build();

            patientRepository.save(patient);
            log.info("Paciente creado: {} {}", patient.getFirstName(), patient.getPaternalSurname());
            return new PatientResponseDTO(patient);

        } catch (OperationException e) {
            log.error("Error de operaciÃ³n al crear paciente: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear paciente", e);
            throw new OperationException("OcurriÃ³ un error inesperado al crear el paciente");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientById(Long id) throws OperationException {
        try {
            Patient patient = findActivePatientById(id);
            return new PatientResponseDTO(patient);
        } catch (OperationException e) {
            log.error("Error al obtener paciente con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al obtener paciente con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al buscar el paciente");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> getAllPatients(Pageable pageable, PatientStatus status, String search) throws OperationException {
        try {
            if (search != null && !search.trim().isEmpty()) {
                return patientRepository.searchByNameOrCi(search.trim(), pageable)
                        .map(PatientResponseDTO::new);
            }
            if (status != null) {
                return patientRepository.findAllByStatus(status, pageable)
                        .map(PatientResponseDTO::new);
            }
            return patientRepository.findAllActive(pageable)
                    .map(PatientResponseDTO::new);
        } catch (Exception e) {
            log.error("Error inesperado al listar pacientes", e);
            throw new OperationException("OcurriÃ³ un error inesperado al listar pacientes");
        }
    }

    @Override
    @Transactional
    public PatientResponseDTO updatePatient(Long id, PatientUpdateRequestDTO request) throws OperationException {
        try {
            ValidationUtil.throwExceptionIfInvalidText("Nombre", request.getFirstName(), true, 80);
            ValidationUtil.throwExceptionIfInvalidText("Apellido paterno", request.getPaternalSurname(), true, 80);
            ValidationUtil.throwExceptionIfInvalidText("Carnet de identidad", request.getCi(), true, 20);

            if (request.getGender() == null) {
                throw new OperationException("El gÃ©nero es requerido");
            }
            if (request.getBirthDate() == null) {
                throw new OperationException("La fecha de nacimiento es requerida");
            }

            Patient patient = findActivePatientById(id);

            if (patientRepository.existsByCiExcludingId(request.getCi().trim(), id)) {
                throw new OperationException(FormatUtil.yaRegistrado("Paciente", "carnet", request.getCi()));
            }

            patient.setFirstName(request.getFirstName().trim());
            patient.setPaternalSurname(request.getPaternalSurname().trim());
            patient.setMaternalSurname(request.getMaternalSurname() != null ? request.getMaternalSurname().trim() : null);
            patient.setCi(request.getCi().trim());
            patient.setGender(request.getGender());
            patient.setBirthDate(request.getBirthDate());
            patient.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
            patient.setEmail(request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null);
            patient.setAddress(request.getAddress() != null ? request.getAddress().trim() : null);
            patient.setBloodType(request.getBloodType());
            patient.setOccupation(request.getOccupation() != null ? request.getOccupation().trim() : null);
            patient.setEmergencyContactName(request.getEmergencyContactName() != null ? request.getEmergencyContactName().trim() : null);
            patient.setEmergencyContactPhone(request.getEmergencyContactPhone() != null ? request.getEmergencyContactPhone().trim() : null);
            patient.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);

            patientRepository.save(patient);
            log.info("Paciente actualizado: ID={}", id);
            return new PatientResponseDTO(patient);

        } catch (OperationException e) {
            log.error("Error al actualizar paciente con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al actualizar paciente con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al actualizar el paciente");
        }
    }

    @Override
    @Transactional
    public PatientResponseDTO changePatientStatus(Long id, PatientStatus status) throws OperationException {
        try {
            if (status == PatientStatus.ELIMINATION) {
                throw new OperationException("No se puede establecer el estado ELIMINATION directamente. Use el endpoint de eliminaciÃ³n.");
            }

            Patient patient = findActivePatientById(id);

            if (patient.getStatus() == status) {
                throw new OperationException("El paciente ya se encuentra en el estado '" + status.getDescription() + "'.");
            }

            patient.setStatus(status);
            patientRepository.save(patient);
            log.info("Estado del paciente ID={} cambiado a: {}", id, status);
            return new PatientResponseDTO(patient);

        } catch (OperationException e) {
            log.error("Error al cambiar estado del paciente con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al cambiar estado del paciente con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al cambiar el estado del paciente");
        }
    }

    @Override
    @Transactional
    public void deletePatient(Long id) throws OperationException {
        try {
            Patient patient = findActivePatientById(id);
            patient.setDeleted(true);
            patient.setStatus(PatientStatus.ELIMINATION);
            patientRepository.save(patient);
            log.info("Paciente con ID={} eliminado lÃ³gicamente", id);
        } catch (OperationException e) {
            log.error("Error al eliminar paciente con ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al eliminar paciente con ID {}", id, e);
            throw new OperationException("OcurriÃ³ un error inesperado al eliminar el paciente");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDTO> getActivePatientsList() throws OperationException {
        try {
            return patientRepository.findAllActiveList()
                    .stream()
                    .map(PatientResponseDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error inesperado al obtener lista de pacientes activos", e);
            throw new OperationException("OcurriÃ³ un error inesperado al obtener la lista de pacientes");
        }
    }

    private Patient findActivePatientById(Long id) throws OperationException {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Paciente", id)));
        if (patient.isDeleted()) {
            throw new OperationException(FormatUtil.noRegistrado("Paciente", id));
        }
        return patient;
    }
}


