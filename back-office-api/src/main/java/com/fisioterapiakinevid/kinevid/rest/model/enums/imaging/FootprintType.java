package com.fisioterapiakinevid.kinevid.rest.model.enums.imaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Clasificación del tipo de huella plantar según la Valoración de Hernández Corvo.
 * El fisioterapeuta selecciona UNA SOLA categoría que representa al paciente en general.
 * Este valor se usa directamente en la generación del reporte PDF.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Getter
@RequiredArgsConstructor
public enum FootprintType {
    INDEX_NORMAL("Índice Normal"),
    INDEX_FLAT_FOOT("Índice Pie Plano"),
    INDEX_CAVUS_FOOT("Índice Pie Cavo"),
    FLAT_FOOT("Pie Plano"),
    FLAT_NORMAL("Pie Plano Normal"),
    NORMAL("Pie Normal"),
    NORMAL_CAVUS("Pie Normal Cavo"),
    CAVUS_FOOT("Pie Cavo"),
    STRONG_CAVUS("Pie Cavo Fuerte"),
    EXTREME_CAVUS("Pie Cavo Extremo");

    private final String description;

    @Override
    public String toString() { return description; }
}
