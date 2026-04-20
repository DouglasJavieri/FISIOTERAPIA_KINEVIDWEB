package com.fisioterapiakinevid.kinevid.rest.model.enums.pat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 * Estados del ciclo de vida de un paciente.
 */
@Getter
@RequiredArgsConstructor
public enum PatientStatus {
    ACTIVE("ACTIVO", "Activo"),
    INACTIVE("INACTIVO", "Inactivo"),
    DISCHARGE("ALTA_MEDICA", "Alta médica"),
    ELIMINATION("ELIMINADO", "Eliminado");

    private final String value;
    private final String description;

    @Override
    public String toString() { return value; }
}

