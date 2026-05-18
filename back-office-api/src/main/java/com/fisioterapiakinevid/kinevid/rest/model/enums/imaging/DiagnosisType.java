package com.fisioterapiakinevid.kinevid.rest.model.enums.imaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Diagnóstico del análisis de pisada.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Getter
@RequiredArgsConstructor
public enum DiagnosisType {
    NORMAL("NORMAL", "Normal"),
    PRONATION("PRONACION", "Pronación"),
    SUPINATION("SUPINACION", "Supinación");

    private final String value;
    private final String description;

    @Override
    public String toString() { return value; }
}
