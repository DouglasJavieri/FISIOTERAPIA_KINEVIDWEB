package com.fisioterapiakinevid.kinevid.rest.service.imaging;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootprintAnalysisRequestDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootprintAnalysisResponseDto;

/**
 * Servicio para gestión de la Valoración de Hernández Corvo.
 * Relación 1:1 con FootAnalysis — máximo una valoración activa por análisis.
 * Utiliza lógica upsert: si ya existe una valoración, se actualiza;
 * si fue eliminada, se reactiva con los nuevos valores.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
public interface FootprintAnalysisService {

    /**
     * Crea o actualiza la Valoración de Hernández Corvo de un análisis de pisada.
     * Si ya existe una valoración activa, actualiza el tipo y notas.
     * Si fue eliminada lógicamente, la reactiva con los nuevos valores.
     * Si no existe, crea un nuevo registro.
     */
    FootprintAnalysisResponseDto saveOrUpdate(Long footAnalysisId, FootprintAnalysisRequestDto request) throws OperationException;

    /**
     * Retorna la valoración activa de un análisis de pisada.
     * Devuelve vacío si aún no fue registrada.
     */
    FootprintAnalysisResponseDto getByFootAnalysisId(Long footAnalysisId) throws OperationException;

    /**
     * Retorna el detalle de una valoración por su ID.
     */
    FootprintAnalysisResponseDto getById(Long id) throws OperationException;

    /**
     * Elimina lógicamente la valoración.
     * Solo permitido si la sesión clínica está ABIERTA.
     */
    void delete(Long id) throws OperationException;
}
