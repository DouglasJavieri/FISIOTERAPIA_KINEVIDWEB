package com.fisioterapiakinevid.kinevid.rest.model.enums.pat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("MASCULINO", "Masculino"),
    FEMALE("FEMENINO", "Femenino"),
    OTHER("OTRO", "Otro");

    private final String value;
    private final String description;

    @Override
    public String toString() { return value; }
}

