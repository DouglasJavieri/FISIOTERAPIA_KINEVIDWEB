package com.fisioterapiakinevid.kinevid.rest.service.report;

import com.fisioterapiakinevid.kinevid.rest.core.report.ArchivoPojo;
import com.fisioterapiakinevid.kinevid.rest.core.report.FormatoReporteEnum;
import com.fisioterapiakinevid.kinevid.rest.core.report.ParametrosJasper;
import com.fisioterapiakinevid.kinevid.rest.core.report.ReportEnum;

import java.util.List;

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
public interface ReportJasperService {
    ArchivoPojo generarReporte(ReportEnum reportEnum, List list, FormatoReporteEnum formato, ParametrosJasper parametrosJasper);
}
