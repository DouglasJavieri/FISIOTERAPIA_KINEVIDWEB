package com.fisioterapiakinevid.kinevid.rest.model.dto.imaging;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para crear o actualizar el análisis biomecánico de un pie (upsert).
 * El campo footSide determina si aplica al pie izquierdo o derecho.
 * Si ya existe un registro para ese pie en el análisis, se actualiza.
 * Si no existe, se crea uno nuevo.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BiomechanicalAnalysisRequestDto {

    @NotNull(message = "El ID del análisis de pisada es requerido")
    private Long footAnalysisId;

    @NotBlank(message = "El lado del pie es requerido")
    private String footSide;

    private String tibialMalleolarRule;

    private String shoeWear;

    private String tibiaPalpation;

    private String gait;
}
