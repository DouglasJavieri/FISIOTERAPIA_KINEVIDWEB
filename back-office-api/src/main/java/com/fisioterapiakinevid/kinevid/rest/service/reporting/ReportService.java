package com.fisioterapiakinevid.kinevid.rest.service.reporting;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;

/**
 * Interfaz para la generación de reportes en el sistema.
 * 
 * @author Douglas Cristhian Javieri Vino
 * @created 06/06/2026
 */
public interface ReportService {

    /**
     * Genera un reporte PDF integral del análisis de pisada.
     * 
     * @param footAnalysisId ID del análisis de pisada.
     * @return Arreglo de bytes del PDF generado.
     * @throws OperationException Si ocurre un error en la generación.
     */
    byte[] generateFootAnalysisReport(Long footAnalysisId) throws OperationException;
}
