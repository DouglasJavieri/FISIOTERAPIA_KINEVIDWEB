package com.fisioterapiakinevid.kinevid.rest.service.imaging;

import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.AnalysisPhotoResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.imaging.AnnotationsUpdateRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Servicio para gestión de fotos del análisis de pisada.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
public interface AnalysisPhotoService {

    /**
     * Sube una foto al proveedor de almacenamiento y la registra en el análisis.
     * Máximo 6 fotos por análisis. El orden debe estar entre 1 y 6.
     */
    AnalysisPhotoResponseDto uploadPhoto(Long footAnalysisId, MultipartFile file, Integer photoOrder) throws OperationException;

    /**
     * Lista todas las fotos activas de un análisis, ordenadas por número de posición.
     */
    List<AnalysisPhotoResponseDto> getPhotosByAnalysisId(Long footAnalysisId) throws OperationException;

    /**
     * Retorna el detalle de una foto por su ID.
     */
    AnalysisPhotoResponseDto getPhotoById(Long id) throws OperationException;

    /**
     * Guarda o actualiza las anotaciones (trazos y ángulos del canvas) de una foto.
     * Solo permitido si la sesión clínica está ABIERTA.
     */
    AnalysisPhotoResponseDto updateAnnotations(Long id, AnnotationsUpdateRequestDto request) throws OperationException;

    /**
     * Alterna el estado de selección de una foto para el reporte PDF.
     * Si estaba seleccionada pasa a no seleccionada y viceversa.
     * Solo permitido si la sesión clínica está ABIERTA.
     */
    AnalysisPhotoResponseDto toggleSelection(Long id) throws OperationException;

    /**
     * Elimina una foto: la borra del proveedor de almacenamiento (Cloudinary)
     * y aplica eliminación lógica en BD.
     * Solo permitido si la sesión clínica está ABIERTA.
     */
    void deletePhoto(Long id) throws OperationException;
}
