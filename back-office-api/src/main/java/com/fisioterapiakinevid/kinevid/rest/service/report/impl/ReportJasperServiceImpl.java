package com.fisioterapiakinevid.kinevid.rest.service.report.impl;

import com.fisioterapiakinevid.kinevid.rest.core.report.*;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.service.report.ReportJasperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.HashMap;
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


@Slf4j
@Service
@RequiredArgsConstructor
public class ReportJasperServiceImpl implements ReportJasperService {

    @Override
    @Transactional(readOnly = true)
    public ArchivoPojo generarReporte(ReportEnum reportEnum, List list, FormatoReporteEnum formato, ParametrosJasper parametrosJasper) {
        log.info("generarReporte inicio {} - {}", reportEnum, formato);
        ArchivoPojo archivoPojo = new ArchivoPojo();
        byte[] byteArray;
        String xmlStr = "";
        String nameReport = "";
        try {
            String jasperPath = "";
            HashMap parametros = new HashMap();

            switch (reportEnum) {
                case REPORT_USER:
                    jasperPath = JasperReportUtil.REPORT_USER;
                    break;
            }

            byteArray = JasperReportUtil.generarReporte(parametros, list, jasperPath, formato,  xmlStr);

            if (formato.equals(FormatoReporteEnum.PDF)) {
                archivoPojo.setNombre(DateUtil.toString(DateUtil.FORMAT_FILE, new Date()) + " - Prosalud" + JasperReportMime.pdfType);
                archivoPojo.setLongitud(byteArray == null ? 0 : byteArray.length);
                archivoPojo.setArchivoBase64(byteArray == null ? null : FileUtil.byteToBase64(byteArray));
                archivoPojo.setTipo(JasperReportMime.pdfType);
                archivoPojo.setTipoMime("application/pdf");
            } else if (formato.equals(FormatoReporteEnum.EXCEL)) {
                archivoPojo.setNombre(DateUtil.toString(DateUtil.FORMAT_FILE, new Date()) + " - Prosalud" + JasperReportMime.excelType);
                archivoPojo.setLongitud(byteArray == null ? 0 : byteArray.length);
                archivoPojo.setArchivoBase64(byteArray == null ? null : FileUtil.byteToBase64(byteArray));
                archivoPojo.setTipo(JasperReportMime.excelType);
                archivoPojo.setTipoMime("application/octet-stream");
            } else if (formato.equals(FormatoReporteEnum.CSV)) {
                archivoPojo.setNombre(DateUtil.toString(DateUtil.FORMAT_FILE, new Date()) + " - Prosalud" + JasperReportMime.csvType);
                archivoPojo.setLongitud(byteArray == null ? 0 : byteArray.length);
                archivoPojo.setArchivoBase64(byteArray == null ? null : FileUtil.byteToBase64(byteArray));
                archivoPojo.setTipo(JasperReportMime.csvType);
                archivoPojo.setTipoMime("text/csv");
            } else if (formato.equals(FormatoReporteEnum.TXTSC)) {
                archivoPojo.setNombre(DateUtil.toString(DateUtil.FORMAT_FILE, new Date()) + " - Prosalud" + JasperReportMime.txtType);
                archivoPojo.setLongitud(byteArray == null ? 0 : byteArray.length);
                archivoPojo.setArchivoBase64(byteArray == null ? null : FileUtil.byteToBase64(byteArray));
                archivoPojo.setTipo(JasperReportMime.txtType);
                archivoPojo.setTipoMime("text/plain");
            }

        } catch (Exception e) {
            log.error("No se pudo generar el reporte:" + list, e);
            throw new OperationException("No se pudo generar reporte [{}]", e.getCause());
        }
        log.info("generarReporte fin {} - {}", reportEnum, formato);

        return archivoPojo;
    }
}
