package com.fisioterapiakinevid.kinevid.rest.core.report;

import java.util.Date;

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
public class DateFromToRange {

    private Date fromDate;
    private Date toDate;

    public DateFromToRange(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }
}

