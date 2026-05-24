package com.fisioterapiakinevid.kinevid.rest.model.enums.svc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
@Getter
@RequiredArgsConstructor
public enum ServiceCategory {
    REHABILITATION("REHABILITACION", "Rehabilitación"),
    ELECTROTHERAPY("ELECTROTERAPIA", "Electroterapia"),
    SPORTS_KINESIOLOGY("KINESIOLOGIA_DEPORTIVA", "Kinesiología Deportiva"),
    THERAPEUTIC_GYMNASIUM("GIMNASIO_TERAPEUTICO", "Gimnasio Terapéutico"),
    POSTURAL_ANALYSIS("ANALISIS_POSTURAL", "Análisis Postural"),
    THERMOTHERAPY("TERMOTERAPIA", "Termoterapia"),
    MANUAL_THERAPY("TERAPIA_MANUAL", "Terapia Manual"),
    KINESIOTHERAPY("KINESIOTERAPIA", "Kinesioterapia "),
    OTHER("OTRO", "Otro");

    private final String value;
    private final String description;

    @Override
    public String toString() { return value; }
}

