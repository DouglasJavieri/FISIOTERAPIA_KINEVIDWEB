package com.fisioterapiakinevid.kinevid.rest.model.dto.clinical;

import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.ClinicalSession;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.SessionStatus;
import lombok.*;

import java.time.LocalDate;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalSessionResponseDTO {

    private Long id;
    private Long episodeId;
    private Integer episodeNumber;
    private Long patientId;
    private String patientFullName;
    private Long employeeId;
    private String employeeFullName;
    private LocalDate sessionDate;
    private Integer sessionNumber;
    private String reasonForConsultation;
    private String relevantBackground;
    private String kinesiologicalEvaluation;
    private String treatmentApplied;
    private String observations;
    private String evolution;
    private Boolean hasImageAnalysis;
    private SessionStatus sessionStatus;

    public ClinicalSessionResponseDTO(ClinicalSession session) {
        this.id = session.getId();
        this.episodeId = session.getEpisode().getId();
        this.episodeNumber = session.getEpisode().getEpisodeNumber();
        this.patientId = session.getEpisode().getPatient().getId();
        this.patientFullName = buildPatientFullName(session);
        this.employeeId = session.getEmployee().getId();
        this.employeeFullName = buildEmployeeFullName(session);
        this.sessionDate = session.getSessionDate();
        this.sessionNumber = session.getSessionNumber();
        this.reasonForConsultation = session.getReasonForConsultation();
        this.relevantBackground = session.getRelevantBackground();
        this.kinesiologicalEvaluation = session.getKinesiologicalEvaluation();
        this.treatmentApplied = session.getTreatmentApplied();
        this.observations = session.getObservations();
        this.evolution = session.getEvolution();
        this.hasImageAnalysis = session.getHasImageAnalysis();
        this.sessionStatus = session.getSessionStatus();
    }

    private String buildPatientFullName(ClinicalSession session) {
        var patient = session.getEpisode().getPatient();
        StringBuilder sb = new StringBuilder(patient.getFirstName());
        sb.append(" ").append(patient.getPaternalSurname());
        if (patient.getMaternalSurname() != null && !patient.getMaternalSurname().isBlank()) {
            sb.append(" ").append(patient.getMaternalSurname());
        }
        return sb.toString();
    }

    private String buildEmployeeFullName(ClinicalSession session) {
        var emp = session.getEmployee();
        StringBuilder sb = new StringBuilder(emp.getFirstName());
        sb.append(" ").append(emp.getPaternalSurname());
        if (emp.getMaternalSurname() != null && !emp.getMaternalSurname().isBlank()) {
            sb.append(" ").append(emp.getMaternalSurname());
        }
        return sb.toString();
    }
}

