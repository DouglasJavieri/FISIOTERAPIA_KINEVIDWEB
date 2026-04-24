package com.fisioterapiakinevid.kinevid.rest.model.enums.clinical;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
@Getter
@RequiredArgsConstructor
public enum SessionStatus {
    OPEN("OPEN","Abierta"),
    CLOSED("CLOSED", "Cerrada"),
    CANCELLED("CANCELLED", "Cancelada");

    private final String value;
    private final String description;

    @Override
    public String toString() {return value;}
}

