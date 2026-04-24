package com.fisioterapiakinevid.kinevid.rest.service.clinical.impl;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalEpisodeRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.ClinicalEpisodeResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.clinical.CloseEpisodeRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.pat.PatientResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.ClinicalEpisode;
import com.fisioterapiakinevid.kinevid.rest.model.entity.pat.Patient;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.EpisodeStatus;
import com.fisioterapiakinevid.kinevid.rest.model.enums.pat.PatientStatus;
import com.fisioterapiakinevid.kinevid.rest.repository.clinical.ClinicalEpisodeRepository;
import com.fisioterapiakinevid.kinevid.rest.service.clinical.ClinicalEpisodeService;
import com.fisioterapiakinevid.kinevid.rest.service.pat.PatientService;
import com.fisioterapiakinevid.kinevid.rest.util.FormatUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ClinicalEpisodeServiceImpl implements ClinicalEpisodeService {

    private final ClinicalEpisodeRepository episodeRepository;
    private final PatientService patientService;

    @Override
    @Transactional
    public ClinicalEpisodeResponseDTO createEpisode(ClinicalEpisodeRequestDTO request) throws OperationException {
        try {
            PatientResponseDTO patientDTO = patientService.getPatientById(request.getPatientId());

            if (patientDTO.getStatus() == PatientStatus.DISCHARGE) {
                throw new OperationException(
                        "El paciente está en estado ALTA. Use el endpoint de reactivación para abrir un nuevo episodio.");
            }

            // Verificar que no tenga episodio activo
            Optional<ClinicalEpisode> activeEpisode =
                    episodeRepository.findActiveEpisodeByPatientId(patientDTO.getId(), EpisodeStatus.ACTIVE);
            if (activeEpisode.isPresent()) {
                throw new OperationException(
                        "El paciente ya tiene un episodio clínico ACTIVO (Ep. " + activeEpisode.get().getEpisodeNumber() + "). " +
                        "Cierre el episodio actual antes de abrir uno nuevo.");
            }

            int nextEpisodeNumber = episodeRepository
                    .findMaxEpisodeNumberByPatientId(patientDTO.getId())
                    .map(max -> max + 1)
                    .orElse(1);

            Patient patientRef = new Patient();
            patientRef.setId(patientDTO.getId());

            ClinicalEpisode episode = ClinicalEpisode.builder()
                    .patient(patientRef)
                    .episodeNumber(nextEpisodeNumber)
                    .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now())
                    .reasonForAdmission(request.getReasonForAdmission().trim())
                    .episodeStatus(EpisodeStatus.ACTIVE)
                    .build();

            episodeRepository.save(episode);
            log.info("Episodio {} creado para paciente ID={}", nextEpisodeNumber, patientDTO.getId());
            return new ClinicalEpisodeResponseDTO(episode);

        } catch (OperationException e) {
            log.error("Error al crear episodio: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al crear episodio", e);
            throw new OperationException("Ocurrió un error inesperado al crear el episodio clínico");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ClinicalEpisodeResponseDTO getEpisodeById(Long id) throws OperationException {
        try {
            ClinicalEpisode episode = findEpisodeById(id);
            return new ClinicalEpisodeResponseDTO(episode);
        } catch (OperationException e) {
            log.error("Error al obtener episodio ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al obtener episodio ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al obtener el episodio clínico");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClinicalEpisodeResponseDTO> getEpisodesByPatientId(Long patientId, Pageable pageable) throws OperationException {
        try {
            patientService.getPatientById(patientId);
            return episodeRepository.findAllByPatientId(patientId, pageable)
                    .map(ClinicalEpisodeResponseDTO::new);
        } catch (OperationException e) {
            log.error("Error al listar episodios del paciente ID={}: {}", patientId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al listar episodios del paciente ID={}", patientId, e);
            throw new OperationException("Ocurrió un error inesperado al listar los episodios clínicos");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClinicalEpisodeResponseDTO> getAllEpisodes(Long patientId, EpisodeStatus status, Pageable pageable) throws OperationException {
        try {
            return episodeRepository.findAllWithFilters(patientId, status, pageable)
                    .map(ClinicalEpisodeResponseDTO::new);
        } catch (Exception e) {
            log.error("Error inesperado al listar episodios globales", e);
            throw new OperationException("Ocurrió un error inesperado al listar los episodios clínicos");
        }
    }

    @Override
    @Transactional
    public ClinicalEpisodeResponseDTO closeEpisode(Long id, CloseEpisodeRequestDTO request) throws OperationException {
        try {
            if (request.getDischargeReason() == null || request.getDischargeReason().isBlank()) {
                throw new OperationException("El motivo del alta es requerido para cerrar el episodio.");
            }

            ClinicalEpisode episode = findEpisodeById(id);

            if (episode.getEpisodeStatus() == EpisodeStatus.CLOSED) {
                throw new OperationException("El episodio ya se encuentra CERRADO.");
            }

            episode.setEpisodeStatus(EpisodeStatus.CLOSED);
            episode.setEndDate(LocalDate.now());
            episode.setDischargeReason(request.getDischargeReason().trim());
            episodeRepository.save(episode);

            patientService.changePatientStatus(episode.getPatient().getId(), PatientStatus.DISCHARGE);

            log.info("Episodio ID={} cerrado. Paciente ID={} en estado DISCHARGE", id, episode.getPatient().getId());
            return new ClinicalEpisodeResponseDTO(episode);

        } catch (OperationException e) {
            log.error("Error al cerrar episodio ID={}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al cerrar episodio ID={}", id, e);
            throw new OperationException("Ocurrió un error inesperado al cerrar el episodio clínico");
        }
    }

    @Override
    @Transactional
    public ClinicalEpisodeResponseDTO reactivatePatient(Long patientId, ClinicalEpisodeRequestDTO request) throws OperationException {
        try {
            PatientResponseDTO patientDTO = patientService.getPatientById(patientId);

            if (patientDTO.getStatus() != PatientStatus.DISCHARGE) {
                throw new OperationException(
                        "Solo se puede reactivar un paciente en estado ALTA. Estado actual: " + patientDTO.getStatus().getDescription());
            }

            // Verificar que no tenga ya episodio activo (consistencia)
            Optional<ClinicalEpisode> activeEpisode =
                    episodeRepository.findActiveEpisodeByPatientId(patientId, EpisodeStatus.ACTIVE);
            if (activeEpisode.isPresent()) {
                throw new OperationException("El paciente ya tiene un episodio clínico ACTIVO.");
            }

            // Reactivar paciente via PatientService
            patientService.changePatientStatus(patientId, PatientStatus.ACTIVE);

            // Crear nuevo episodio
            int nextEpisodeNumber = episodeRepository
                    .findMaxEpisodeNumberByPatientId(patientId)
                    .map(max -> max + 1)
                    .orElse(1);

            if (request.getReasonForAdmission() == null || request.getReasonForAdmission().isBlank()) {
                throw new OperationException("El motivo de ingreso es requerido para el nuevo episodio.");
            }

            Patient patientRef = new Patient();
            patientRef.setId(patientId);

            ClinicalEpisode newEpisode = ClinicalEpisode.builder()
                    .patient(patientRef)
                    .episodeNumber(nextEpisodeNumber)
                    .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now())
                    .reasonForAdmission(request.getReasonForAdmission().trim())
                    .episodeStatus(EpisodeStatus.ACTIVE)
                    .build();

            episodeRepository.save(newEpisode);
            log.info("Paciente ID={} reactivado. Nuevo episodio {} creado.", patientId, nextEpisodeNumber);
            return new ClinicalEpisodeResponseDTO(newEpisode);

        } catch (OperationException e) {
            log.error("Error al reactivar paciente ID={}: {}", patientId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al reactivar paciente ID={}", patientId, e);
            throw new OperationException("Ocurrió un error inesperado al reactivar el paciente");
        }
    }


    private ClinicalEpisode findEpisodeById(Long id) throws OperationException {
        ClinicalEpisode episode = episodeRepository.findById(id)
                .orElseThrow(() -> new OperationException(FormatUtil.noRegistrado("Episodio Clínico", id)));
        if (episode.isDeleted()) {
            throw new OperationException(FormatUtil.noRegistrado("Episodio Clínico", id));
        }
        return episode;
    }
}
