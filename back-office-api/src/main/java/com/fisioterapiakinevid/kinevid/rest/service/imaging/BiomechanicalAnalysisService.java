package com.fisioterapiakinevid.kinevid.rest.service.imaging;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.BiomechanicalAnalysisRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.BiomechanicalAnalysisResponseDto;

import java.util.List;

/**
 * Servicio para gestión del análisis biomecánico por pie (LEFT / RIGHT).
 * Utiliza lógica upsert: si ya existe un registro para el pie indicado, se actualiza;
 * si no existe (o fue eliminado), se crea (o reactiva).
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
public interface BiomechanicalAnalysisService {

    /**
     * Crea o actualiza el análisis biomecánico de un pie dentro de un análisis de pisada.
     * Si ya existe un registro para ese {@code footSide}, se actualizan sus campos.
     * Si fue eliminado lógicamente, se reactiva y se actualizan los campos.
     * Si no existe, se crea uno nuevo.
     */
    BiomechanicalAnalysisResponseDto saveOrUpdate(Long footAnalysisId, BiomechanicalAnalysisRequestDto request) throws OperationException;

    /**
     * Lista los análisis biomecánicos activos de un análisis de pisada.
     * Devuelve máximo 2 registros (LEFT y RIGHT).
     */
    List<BiomechanicalAnalysisResponseDto> getByFootAnalysisId(Long footAnalysisId) throws OperationException;

    /**
     * Retorna el detalle de un análisis biomecánico por su ID.
     */
    BiomechanicalAnalysisResponseDto getById(Long id) throws OperationException;

    /**
     * Elimina lógicamente un análisis biomecánico.
     * Solo permitido si la sesión clínica está ABIERTA.
     */
    void delete(Long id) throws OperationException;
}
