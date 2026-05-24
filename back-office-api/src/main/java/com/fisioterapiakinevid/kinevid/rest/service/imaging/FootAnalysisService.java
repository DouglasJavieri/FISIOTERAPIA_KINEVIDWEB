package com.fisioterapiakinevid.kinevid.rest.service.imaging;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootAnalysisRequestDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootAnalysisResponseDTO;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.FootAnalysisUpdateRequestDTO;

/**
 * Servicio para gestión de análisis de pisada (FootAnalysis).
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
public interface FootAnalysisService {

    FootAnalysisResponseDTO createFootAnalysis(FootAnalysisRequestDTO request) throws OperationException;

    FootAnalysisResponseDTO getFootAnalysisById(Long id) throws OperationException;

    FootAnalysisResponseDTO getFootAnalysisBySessionId(Long sessionId) throws OperationException;

    FootAnalysisResponseDTO updateFootAnalysis(Long id, FootAnalysisUpdateRequestDTO request) throws OperationException;

    void deleteFootAnalysis(Long id) throws OperationException;
}
