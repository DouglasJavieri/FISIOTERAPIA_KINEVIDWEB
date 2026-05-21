package com.fisioterapiakinevid.kinevid.rest.model.dto.imaging;

import lombok.*;

/**
 * DTO de respuesta para la Valoración de Hernández Corvo.
 * El tipo de huella se expone con su nombre interno Java (footprintType)
 * y su descripción en español (footprintTypeDescription) para que el
 * frontend muestre el texto correcto sin depender de los nombres del enum.
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
public class FootprintAnalysisResponseDto {

    private Long id;
    private Long footAnalysisId;

    // — Clasificación de Hernández Corvo —
    private String footprintType;
    private String footprintTypeDescription;

    // — Notas del evaluador —
    private String notes;
}
