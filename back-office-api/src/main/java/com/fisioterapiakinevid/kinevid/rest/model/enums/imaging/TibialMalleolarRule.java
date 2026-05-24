package com.fisioterapiakinevid.kinevid.rest.model.enums.imaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Clasificación de la regla maleolo tibial en el análisis biomecánico del pie.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Getter
@RequiredArgsConstructor
public enum TibialMalleolarRule {
    NORMAL("NORMAL", "Normal"),
    VARUS("VARO", "Varo"),
    VALGUS("VALGO", "Valgo"),
    OTHER("OTRO", "Otro");

    private final String value;
    private final String description;

    @Override
    public String toString() { return value; }
}
