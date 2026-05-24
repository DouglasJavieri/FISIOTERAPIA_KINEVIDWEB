package com.fisioterapiakinevid.kinevid.rest.model.dto.imaging;

import lombok.*;

/**
 * DTO de respuesta para BiomechanicalAnalysis.
 * Los enums se exponen como String (value) y String (description)
 * para que el frontend no dependa de los nombres internos de Java.
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
public class BiomechanicalAnalysisResponseDto {

    private Long id;
    private Long footAnalysisId;

    // — Lado del pie —
    private String footSide;
    private String footSideDescription;

    // — Regla maleolo tibial —
    private String tibialMalleolarRule;
    private String tibialMalleolarRuleDescription;

    // — Datos clínicos —
    private String shoeWear;
    private String tibiaPalpation;

    // — Marcha —
    private String gait;
    private String gaitDescription;
}
