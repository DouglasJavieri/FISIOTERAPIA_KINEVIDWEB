package com.fisioterapiakinevid.kinevid.rest.model.enums.emp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 08/04/2026
 * Departamentos de Bolivia. Usado para los campos expedition y department del empleado.
 */
@Getter
@RequiredArgsConstructor
public enum BolivianDepartment {
    BENI("Beni"),
    CHUQUISACA("Chuquisaca"),
    COCHABAMBA("Cochabamba"),
    LA_PAZ("La Paz"),
    ORURO("Oruro"),
    PANDO("Pando"),
    POTOSI("Potosí"),
    SANTA_CRUZ("Santa Cruz"),
    TARIJA("Tarija");

    private final String description;

    @Override
    public String toString() { return description; }
}

