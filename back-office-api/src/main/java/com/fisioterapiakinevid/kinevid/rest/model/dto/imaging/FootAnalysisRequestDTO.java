package com.fisioterapiakinevid.kinevid.rest.model.dto.imaging;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para crear un nuevo análisis de pisada dentro de una sesión clínica.
 * Solo se requiere el ID de la sesión y el diagnóstico al crear.
 * Los demás campos se pueden completar progresivamente (update).
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FootAnalysisRequestDTO {

    @NotNull(message = "El ID de la sesión clínica es requerido")
    private Long clinicalSessionId;

    @NotNull(message = "El diagnóstico es requerido")
    private String diagnosis;

    private String relevantBackground;

    private String kinesiologicalEvaluation;

    private String observations;

    private BigDecimal distanceIntermaleolar;

    private BigDecimal distanceIntercondylar;

    private BigDecimal angleLeftInternal;

    private BigDecimal angleLeftExternal;

    private BigDecimal angleRightInternal;

    private BigDecimal angleRightExternal;
}
