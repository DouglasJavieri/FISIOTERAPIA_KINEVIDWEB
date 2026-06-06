package com.fisioterapiakinevid.kinevid.rest.model.dto.imaging;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para guardar todo el flujo de análisis de pisada en una sola operación (Upsert).
 * Incluye datos generales, biomecánica y huella plantar.
 * Las fotos se manejan por separado por ser archivos.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 06/06/2026
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FootAnalysisFullSaveRequestDTO {

    @NotNull(message = "El ID de la sesión clínica es requerido")
    private Long clinicalSessionId;

    // --- Datos Generales (FootAnalysis) ---
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

    // --- Biomecánica (Combinada en un solo DTO o campos directos) ---
    // Pie Izquierdo
    private String leftTibialMalleolarRule;
    private String leftShoeWear;
    private String leftTibiaPalpation;
    private String leftGait;

    // Pie Derecho
    private String rightTibialMalleolarRule;
    private String rightShoeWear;
    private String rightTibiaPalpation;
    private String rightGait;

    // --- Huella Plantar (FootprintAnalysis) ---
    private String footprintType;
}
