package com.fisioterapiakinevid.kinevid.rest.core.report;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

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
public class JasperReportUtil {

    static final Logger LOGGER = LoggerFactory.getLogger(JasperReportUtil.class);

    public static final String REPORT_USER = "reports/RepUser.jrxml";
    public static String getJrxml(String pathFile) throws IOException {
        String xmlStr;
        InputStream fonte = new ClassPathResource(pathFile).getInputStream();
        xmlStr = IOUtils.toString(fonte, StandardCharsets.UTF_8);
        return  xmlStr;
    }


    public static byte[] generarReporte(HashMap parametros, Collection<?> lista, String jasperPath, FormatoReporteEnum formato, String jrxml) {
        byte[] byteArray = null;
        try {
            if (StringUtil.isBlank(jrxml)) {
                jrxml = getJrxml(jasperPath);
            }

            if (formato.equals(FormatoReporteEnum.PDF)) {
                byteArray = generarReportePdf(parametros, lista, jrxml);
            }
            if (formato.equals(FormatoReporteEnum.EXCEL)) {
                byteArray = generarReporteExcel(parametros, lista, jrxml);
            }
            if (formato.equals(FormatoReporteEnum.CSV)) {
                byteArray = generarReporteCSV(parametros, lista, jrxml);
            }
            if (formato.equals(FormatoReporteEnum.TXTSC)) {
                byteArray = generarReporteTXTSC(parametros, lista, jrxml);
            }
            if (formato.equals(FormatoReporteEnum.TR3))  {
                byteArray = generarReporteTR3(parametros, lista, jrxml);
            }
        } catch (Exception e) {
            LOGGER.error("Error al obtener archivo jasper:{}", jasperPath, e);
        }
        return byteArray;
    }

    private static byte[] generarReportePdf(HashMap parametros, Collection<?> lista, String jrxlm) {
        byte[] byteArray = null;
        try {
            JasperReport report = JasperCompileManager.compileReport(new ByteArrayInputStream(jrxlm.getBytes(StandardCharsets.UTF_8)));
            JasperPrint jprint = JasperFillManager.fillReport(report, parametros, new JRBeanCollectionDataSource(lista));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jprint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
            exporter.exportReport();
            byteArray = byteArrayOutputStream.toByteArray();
        } catch (JRException e) {
            LOGGER.error("Error de JRException al generar el reporte", e);
        } catch (Exception e) {
            LOGGER.error("Error generico al generar reporte ", e);
        }
        return byteArray;
    }

    private static byte[] generarReporteExcel(HashMap parametros, Collection<?> lista, String xrlm) {
        byte[] byteArray = null;
        try {
            JasperReport report = JasperCompileManager.compileReport(new ByteArrayInputStream(xrlm.getBytes(StandardCharsets.UTF_8)));
            java.util.Locale locale = new Locale("en", "US");
            parametros.put(JRParameter.REPORT_LOCALE, locale);
            JasperPrint jprint = JasperFillManager.fillReport(report, parametros, new JRBeanCollectionDataSource(lista));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
            reportConfig.setSheetNames(new String[]{"Datos"});
            reportConfig.setCollapseRowSpan(true);
            reportConfig.setRemoveEmptySpaceBetweenColumns(true);
            reportConfig.setRemoveEmptySpaceBetweenRows(true);
            reportConfig.setOnePagePerSheet(false);
            reportConfig.setMaxRowsPerSheet(100000);
            reportConfig.setDetectCellType(false);
            reportConfig.setWhitePageBackground(false);
            reportConfig.setIgnoreGraphics(false);
            reportConfig.setDetectCellType(true);
            JRXlsxExporter exporterXLS = new JRXlsxExporter();
            exporterXLS.setExporterInput(new SimpleExporterInput(jprint));
            exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
            exporterXLS.setConfiguration(reportConfig);
            exporterXLS.exportReport();
            byteArray = byteArrayOutputStream.toByteArray();
        } catch (JRException e) {
            LOGGER.debug("jrxml: " + xrlm);
            LOGGER.error("Error de JRException al generar el reporte", e);
        } catch (Exception e) {
            LOGGER.error("Error generico al generar reporte ", e);
        }
        return byteArray;
    }


    private static byte[] generarReporteCSV(HashMap<String, Object> parametros, Collection<?> lista, String xrlm) {

        LOGGER.info("----------------------------------------------");
        LOGGER.info("Inicio Generando reporte CSV Módulo");
        LOGGER.info("----------------------------------------------");
        byte[] byteArray = null;

        try {
            JasperReport report = JasperCompileManager.compileReport(new ByteArrayInputStream(xrlm.getBytes(StandardCharsets.UTF_8)));
            java.util.Locale locale = new Locale("en", "US");
            parametros.put(JRParameter.REPORT_LOCALE, locale);
            JasperPrint jprint = JasperFillManager.fillReport(report, parametros, new JRBeanCollectionDataSource(lista));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            LOGGER.debug("Exportando reporte {} a CSV", parametros.get("tipoDocumentoSector"));
            JRCsvExporter exporterCSV = new JRCsvExporter();
            exporterCSV.setExporterInput(new SimpleExporterInput(jprint));
            exporterCSV.setExporterOutput(new SimpleWriterExporterOutput(byteArrayOutputStream));
            SimpleCsvExporterConfiguration csvConfig = new SimpleCsvExporterConfiguration();
            csvConfig.setFieldDelimiter(",");
            csvConfig.setRecordDelimiter("\n");
            exporterCSV.setConfiguration(csvConfig);
            exporterCSV.exportReport();
            byteArray = byteArrayOutputStream.toByteArray();

        } catch (JRException e) {
            LOGGER.debug("jrxml: " + xrlm);
            LOGGER.error("Error de JRException al generar el reporte CSV", e);
        } catch (Exception e) {
            LOGGER.error("Error generico al generar reporte CSV", e);
        }

        LOGGER.info("----------------------------------------------");
        LOGGER.info("Fin Generando reporte CSV Módulo:");
        LOGGER.info("----------------------------------------------");

        return byteArray;
    }

    private static byte[] generarReporteTXTSC(HashMap<String, Object> parametros, Collection<?> lista, String xrlm) {
        LOGGER.info("----------------------------------------------");
        LOGGER.info("Inicio Generando reporte TXT Módulo");
        LOGGER.info("----------------------------------------------");
        byte[] byteArray = null;

        try {
            JasperReport report = JasperCompileManager.compileReport(new ByteArrayInputStream(xrlm.getBytes(StandardCharsets.UTF_8)));
            java.util.Locale locale = new Locale("en", "US");
            parametros.put(JRParameter.REPORT_LOCALE, locale);
            JasperPrint jprint = JasperFillManager.fillReport(report, parametros, new JRBeanCollectionDataSource(lista));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            LOGGER.debug("Exportando reporte a TXT");
            JRCsvExporter exporterTXT = new JRCsvExporter();
            exporterTXT.setExporterInput(new SimpleExporterInput(jprint));
            exporterTXT.setExporterOutput(new SimpleWriterExporterOutput(byteArrayOutputStream));
            SimpleCsvExporterConfiguration txtConfig = new SimpleCsvExporterConfiguration();
            txtConfig.setFieldDelimiter(";");
            txtConfig.setRecordDelimiter("\n");
            exporterTXT.setConfiguration(txtConfig);
            exporterTXT.exportReport();
            byteArray = byteArrayOutputStream.toByteArray();

        } catch (JRException e) {
            LOGGER.debug("jrxml: " + xrlm);
            LOGGER.error("Error de JRException al generar el reporte TXT", e);
        } catch (Exception e) {
            LOGGER.error("Error generico al generar reporte TXT", e);
        }

        LOGGER.info("----------------------------------------------");
        LOGGER.info("Fin Generando reporte TXT Módulo");
        LOGGER.info("----------------------------------------------");
        return byteArray;
    }

    private static byte[] generarReporteTR3(HashMap<String, Object> parametros, Collection<?> lista, String xrlm) {
        LOGGER.info("----------------------------------------------");
        LOGGER.info("Inicio Generando reporte TR3 Módulo");
        LOGGER.info("----------------------------------------------");
        byte[] byteArray = null;

        try {
            JasperReport report = JasperCompileManager.compileReport(
                    new ByteArrayInputStream(xrlm.getBytes(StandardCharsets.UTF_8)));

            java.util.Locale locale = new Locale("en", "US");
            parametros.put(JRParameter.REPORT_LOCALE, locale);

            JasperPrint jprint = JasperFillManager.fillReport(report, parametros, new JRBeanCollectionDataSource(lista));

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            LOGGER.debug("Exportando reporte a TR3 (formato |)");

            JRCsvExporter exporterTR3 = new JRCsvExporter();
            exporterTR3.setExporterInput(new SimpleExporterInput(jprint));
            exporterTR3.setExporterOutput(new SimpleWriterExporterOutput(byteArrayOutputStream));

            SimpleCsvExporterConfiguration tr3Config = new SimpleCsvExporterConfiguration();
            tr3Config.setFieldDelimiter("|");
            tr3Config.setRecordDelimiter("\n");
            tr3Config.setWriteBOM(Boolean.FALSE);
            exporterTR3.setConfiguration(tr3Config);
            exporterTR3.exportReport();
            byteArray = byteArrayOutputStream.toByteArray();

        } catch (JRException e) {
            LOGGER.debug("jrxml: " + xrlm);
            LOGGER.error("Error de JRException al generar el reporte TR3", e);
        } catch (Exception e) {
            LOGGER.error("Error generico al generar reporte TR3", e);
        }

        LOGGER.info("----------------------------------------------");
        LOGGER.info("Fin Generando reporte TR3 Módulo");
        LOGGER.info("----------------------------------------------");
        return byteArray;
    }
}
