package com.fisioterapiakinevid.kinevid.rest.model.enums.imaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Lado del pie evaluado en el análisis biomecánico.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Getter
@RequiredArgsConstructor
public enum FootSide {
    LEFT("IZQUIERDO", "Pie Izquierdo"),
    RIGHT("DERECHO", "Pie Derecho");

    private final String value;
    private final String description;

    @Override
    public String toString() { return value; }
}
