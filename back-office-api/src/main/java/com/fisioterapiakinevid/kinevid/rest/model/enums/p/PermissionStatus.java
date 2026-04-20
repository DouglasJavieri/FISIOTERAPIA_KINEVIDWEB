package com.fisioterapiakinevid.kinevid.rest.model.enums.p;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 01/04/2026
 * Estados posibles de un permiso del sistema.
 * ELIMINATION solo se asigna a través del endpoint de eliminación lógica.
 */
@Getter
@RequiredArgsConstructor
public enum PermissionStatus {
    ACTIVE("ACTIVO", "Activo"),
    INACTIVE("INACTIVO", "Inactivo"),
    ELIMINATION("ELIMINADO", "Eliminado");

    private final String value;
    private final String description;

    @Override
    public String toString() { return value; }
}

