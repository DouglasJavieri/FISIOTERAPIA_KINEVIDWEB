package com.fisioterapiakinevid.kinevid.rest.model.enums.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/02/2026
 */
@Getter
@RequiredArgsConstructor
public enum RoleStatus {
    ACTIVE("ACTIVO", "Activo"),
    INACTIVE("INACTIVO", "Inactivo"),
    ELIMINATION("ELIMINADO", "Eliminado");

    private final String value;
    private final String description;

    @Override
    public String toString() {return value;}
}
