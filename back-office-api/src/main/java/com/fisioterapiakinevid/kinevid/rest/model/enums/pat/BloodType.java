package com.fisioterapiakinevid.kinevid.rest.model.enums.pat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
@Getter
@RequiredArgsConstructor
public enum BloodType {
    A_POSITIVE("A+", "A Positivo"),
    A_NEGATIVE("A-", "A Negativo"),
    B_POSITIVE("B+", "B Positivo"),
    B_NEGATIVE("B-", "B Negativo"),
    AB_POSITIVE("AB+", "AB Positivo"),
    AB_NEGATIVE("AB-", "AB Negativo"),
    O_POSITIVE("O+", "O Positivo"),
    O_NEGATIVE("O-", "O Negativo"),
    UNKNOWN("DESCONOCIDO", "Desconocido");

    private final String value;
    private final String description;

    @Override
    public String toString() { return value; }
}

