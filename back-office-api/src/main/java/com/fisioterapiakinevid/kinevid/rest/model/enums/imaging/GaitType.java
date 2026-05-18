package com.fisioterapiakinevid.kinevid.rest.model.enums.imaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Clasificación del patrón de marcha evaluado en el análisis biomecánico del pie.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Getter
@RequiredArgsConstructor
public enum GaitType {
    NORMAL("NORMAL", "Normal"),
    PRONATOR("PRONADOR", "Pronador"),
    SUPINATOR("SUPINADOR", "Supinador"),
    MIXED("MIXTO", "Mixto");

    private final String value;
    private final String description;

    @Override
    public String toString() { return value; }
}
