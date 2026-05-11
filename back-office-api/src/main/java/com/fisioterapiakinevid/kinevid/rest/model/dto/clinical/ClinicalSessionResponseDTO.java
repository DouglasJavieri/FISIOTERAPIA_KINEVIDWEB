package com.fisioterapiakinevid.kinevid.rest.model.dto.clinical;

import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.ClinicalSession;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.SessionStatus;
import lombok.*;

import java.time.LocalDate;

/**
 * @author Douglas Cristhian Javieri Vino
 * Creado: 23/04/2026
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
    private String actualIllnessHistory;
    private String gait;
    private String functionalTests;
    private String complementaryExams;
    private String kinesiologicalDiagnosis;
    private String treatmentApplied;
    private String observations;
    private String evolution;
    private Boolean hasImageAnalysis;
    private SessionStatus sessionStatus;

    public ClinicalSessionResponseDTO(ClinicalSession session) {
        this.id = session.getId();
        this.episodeId = session.getEpisode() != null ? session.getEpisode().getId() : null;
        this.episodeNumber = session.getEpisode() != null ? session.getEpisode().getEpisodeNumber() : null;
        this.patientId = session.getEpisode() != null && session.getEpisode().getPatient() != null
                ? session.getEpisode().getPatient().getId()
                : null;
        this.patientFullName = buildPatientFullName(session);
        this.employeeId = session.getEmployee() != null ? session.getEmployee().getId() : null;
        this.employeeFullName = buildEmployeeFullName(session);
        this.sessionDate = session.getSessionDate();
        this.sessionNumber = session.getSessionNumber();
        this.reasonForConsultation = session.getReasonForConsultation();
        this.relevantBackground = session.getRelevantBackground();
        this.kinesiologicalEvaluation = session.getKinesiologicalEvaluation();
        this.actualIllnessHistory = session.getActualIllnessHistory();
        this.gait = session.getGait();
        this.functionalTests = session.getFunctionalTests();
        this.complementaryExams = session.getComplementaryExams();
        this.kinesiologicalDiagnosis = session.getKinesiologicalDiagnosis();
        this.treatmentApplied = session.getTreatmentApplied();
        this.observations = session.getObservations();
        this.evolution = session.getEvolution();
        this.hasImageAnalysis = session.getHasImageAnalysis();
        this.sessionStatus = session.getSessionStatus();
    }

    private String buildPatientFullName(ClinicalSession session) {
        if (session.getEpisode() == null || session.getEpisode().getPatient() == null) {
            return null;
        }
        var patient = session.getEpisode().getPatient();
        String firstName = patient.getFirstName() != null ? patient.getFirstName() : "";
        String paternalSurname = patient.getPaternalSurname() != null ? patient.getPaternalSurname() : "";
        StringBuilder sb = new StringBuilder(firstName);
        if (!paternalSurname.isBlank()) {
            sb.append(" ").append(paternalSurname);
        }
        if (patient.getMaternalSurname() != null && !patient.getMaternalSurname().isBlank()) {
            sb.append(" ").append(patient.getMaternalSurname());
        }
        return sb.toString().trim();
    }

    private String buildEmployeeFullName(ClinicalSession session) {
        if (session.getEmployee() == null) {
            return null;
        }
        var emp = session.getEmployee();
        String firstName = emp.getFirstName() != null ? emp.getFirstName() : "";
        String paternalSurname = emp.getPaternalSurname() != null ? emp.getPaternalSurname() : "";
        StringBuilder sb = new StringBuilder(firstName);
        if (!paternalSurname.isBlank()) {
            sb.append(" ").append(paternalSurname);
        }
        if (emp.getMaternalSurname() != null && !emp.getMaternalSurname().isBlank()) {
            sb.append(" ").append(emp.getMaternalSurname());
        }
        return sb.toString().trim();
    }
}

