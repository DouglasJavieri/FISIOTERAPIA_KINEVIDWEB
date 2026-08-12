package com.fisioterapiakinevid.kinevid.rest.core.report;

import lombok.Builder;
import lombok.Getter;

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
@Getter
@Builder
public class ParametrosJasper {
    private String author;
    private String module;
    private String fileName;
    private String auditTable;
    private String fromDate;
    private String toDate;
    private String titleReport;
    private String user;
    private Date startDate;
    private Date endDate;
    private String reason;
    private Integer vcPlannedYear;
    private String nameUser;
    private String gender;
    private String contract;
    private String filterDate;
    private Short year;
    private Short month;
    private String nameMonth;
    private String nameEmployee;
    private String totalPerformance;
}

