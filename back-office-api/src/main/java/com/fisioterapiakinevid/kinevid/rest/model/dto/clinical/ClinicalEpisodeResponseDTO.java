package com.fisioterapiakinevid.kinevid.rest.model.dto.clinical;

import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.ClinicalEpisode;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.EpisodeStatus;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO de respuesta para ClinicalEpisode.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalEpisodeResponseDTO {

    private Long id;
    private Long patientId;
    private String patientFullName;
    private String patientCi;
    private Integer episodeNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reasonForAdmission;
    private String dischargeReason;
    private EpisodeStatus episodeStatus;

    public ClinicalEpisodeResponseDTO(ClinicalEpisode episode) {
        this.id = episode.getId();
        this.patientId = episode.getPatient().getId();
        this.patientFullName = buildFullName(episode);
        this.patientCi = episode.getPatient().getCi();
        this.episodeNumber = episode.getEpisodeNumber();
        this.startDate = episode.getStartDate();
        this.endDate = episode.getEndDate();
        this.reasonForAdmission = episode.getReasonForAdmission();
        this.dischargeReason = episode.getDischargeReason();
        this.episodeStatus = episode.getEpisodeStatus();
    }

    private String buildFullName(ClinicalEpisode episode) {
        StringBuilder sb = new StringBuilder(episode.getPatient().getFirstName());
        sb.append(" ").append(episode.getPatient().getPaternalSurname());
        if (episode.getPatient().getMaternalSurname() != null && !episode.getPatient().getMaternalSurname().isBlank()) {
            sb.append(" ").append(episode.getPatient().getMaternalSurname());
        }
        return sb.toString();
    }
}

