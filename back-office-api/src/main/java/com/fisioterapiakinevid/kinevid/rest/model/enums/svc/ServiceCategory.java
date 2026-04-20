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
    SPORTS_KINESIOLOGY("KINESIOLOGIA_DEPORTIVA", "Kinesiología Deportiva"),
    MASSOTHERAPY("MASOTERAPIA", "Masoterapia"),
    POSTURAL_ANALYSIS("ANALISIS_POSTURAL", "Análisis Postural"),
    PEDIATRIC_KINESIOLOGY("KINESIOLOGIA_PEDIATRICA", "Kinesiología Pediátrica"),
    NEUROLOGICAL_REHABILITATION("REHABILITACION_NEUROLOGICA", "Rehabilitación Neurológica"),
    OTHER("OTRO", "Otro");

    private final String value;
    private final String description;

    @Override
    public String toString() { return value; }
}

