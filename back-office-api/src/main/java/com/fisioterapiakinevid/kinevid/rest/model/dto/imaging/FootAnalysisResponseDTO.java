package com.fisioterapiakinevid.kinevid.rest.model.dto.imaging;

import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.DiagnosisType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para FootAnalysis.
 * Incluye datos contextuales de la sesión, episodio y paciente.
 * Sin lógica de mapeo — el mapeo se realiza en el ServiceImpl.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FootAnalysisResponseDTO {

    // — Identificación del análisis —
    private Long id;
    private LocalDateTime analysisDate;

    // — Contexto clínico —
    private Long clinicalSessionId;
    private Integer sessionNumber;
    private Long episodeId;
    private Integer episodeNumber;
    private Long patientId;
    private String patientFullName;
    private String patientCi;
    private Long employeeId;
    private String employeeFullName;

    // — Datos del análisis —
    private String relevantBackground;
    private String kinesiologicalEvaluation;
    private String observations;
    private DiagnosisType diagnosis;

    // — Mediciones —
    private BigDecimal distanceIntermaleolar;
    private BigDecimal distanceIntercondylar;
    private BigDecimal angleLeftInternal;
    private BigDecimal angleLeftExternal;
    private BigDecimal angleRightInternal;
    private BigDecimal angleRightExternal;

    // — Estado de la sesión (para control de edición en frontend) —
    private String sessionStatus;
}
