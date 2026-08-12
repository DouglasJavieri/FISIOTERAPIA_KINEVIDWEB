package com.fisioterapiakinevid.kinevid.rest.core.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 *----------------------------------------
 *   Código de Aplicación:
 *   Código de Objeto:
 *   Descripción:
 *   Author Prog: Jorge Luis Choque Callizaya
 *----------------------------------------
 *   Fecha | Autor | Comentario
 *   12.08.2026 | Jorge Luis Choque Callizaya | Creación Inicial
 *----------------------------------------
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoPojo {

    private String nombre;
    private long longitud;
    private String tipo;
    private String tipoMime;
    private String archivoBase64;
}
