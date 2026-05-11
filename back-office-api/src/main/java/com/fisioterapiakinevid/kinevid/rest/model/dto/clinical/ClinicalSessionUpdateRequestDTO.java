package com.fisioterapiakinevid.kinevid.rest.model.dto.clinical;
import lombok.*;
/**
 * DTO para actualizar los campos clinicos de una sesion (solo si esta OPEN).
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalSessionUpdateRequestDTO {
    private Long employeeId;
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
}