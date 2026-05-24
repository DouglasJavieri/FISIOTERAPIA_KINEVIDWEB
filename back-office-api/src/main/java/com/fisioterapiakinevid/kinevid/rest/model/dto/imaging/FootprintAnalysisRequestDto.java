package com.fisioterapiakinevid.kinevid.rest.model.dto.imaging;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO para crear o actualizar la Valoración de Hernández Corvo (upsert).
 * El footAnalysisId proviene del path variable del controller, no del body.
 * Si ya existe una valoración para el análisis, se actualiza.
 * Si fue eliminada lógicamente, se reactiva con los nuevos valores.
 * Si no existe, se crea una nueva.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FootprintAnalysisRequestDto {

    @NotBlank(message = "El tipo de huella plantar es requerido")
    private String footprintType;

    private String notes;
}
