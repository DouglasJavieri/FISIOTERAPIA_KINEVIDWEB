package com.fisioterapiakinevid.kinevid.rest.model.dto.imaging;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para actualizar un análisis de pisada existente (solo si la sesión está OPEN).
 * Todos los campos son opcionales — solo se actualizan los que se envían.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FootAnalysisUpdateRequestDTO {

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
