package com.fisioterapiakinevid.kinevid.rest.core.report;

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
public enum FormatoReporteEnum {


    PDF("PDF"),
    EXCEL("EXCEL"),
    CSV("CSV"),
    TXTSC("TEXTSC"),
    TR3("TR3");


    private String formato;

    private FormatoReporteEnum(String formato){
        this.formato = formato;
    }
}
