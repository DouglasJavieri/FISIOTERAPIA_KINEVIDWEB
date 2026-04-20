package com.fisioterapiakinevid.kinevid.rest.model.enums.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 01/02/2026
 */
@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE("ACTIVO", "Activo"),
    INACTIVE("INACTIVO", "Inactivo"),
    ELIMINATION("ELIMINADO", "Eliminado");

    private final String value;
    private final String description;

    @Override
    public String toString() {return value;}
}
