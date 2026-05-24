package com.fisioterapiakinevid.kinevid.rest.repository.imaging;

import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.AnalysisPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad AnalysisPhoto.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Repository
public interface AnalysisPhotoRepository extends JpaRepository<AnalysisPhoto, Long> {

    /**
     * Busca una foto por ID con su análisis de pisada cargado.
     * Útil para validar pertenencia y cargar contexto completo.
     */
    @Query("SELECT ap FROM AnalysisPhoto ap " +
           "JOIN FETCH ap.footAnalysis fa " +
           "JOIN FETCH fa.clinicalSession cs " +
           "WHERE ap.id = :id AND ap.deleted = false")
    Optional<AnalysisPhoto> findByIdWithRelations(@Param("id") Long id);

    /**
     * Lista todas las fotos activas de un análisis, ordenadas por número de posición.
     * Orden ascendente para que el frontend reciba las fotos en el orden correcto.
     */
    @Query("SELECT ap FROM AnalysisPhoto ap " +
           "WHERE ap.footAnalysis.id = :footAnalysisId AND ap.deleted = false " +
           "ORDER BY ap.photoOrder ASC")
    List<AnalysisPhoto> findAllByFootAnalysisId(@Param("footAnalysisId") Long footAnalysisId);

    /**
     * Cuenta las fotos activas de un análisis.
     * Usado para validar el límite de 6 fotos antes de subir una nueva.
     */
    long countByFootAnalysisIdAndDeletedFalse(Long footAnalysisId);

    /**
     * Verifica si ya existe una foto con el mismo número de orden en el análisis.
     * Usado para evitar duplicar el mismo slot de posición.
     */
    boolean existsByFootAnalysisIdAndPhotoOrderAndDeletedFalse(Long footAnalysisId, Integer photoOrder);

    /**
     * Lista todas las fotos marcadas como seleccionadas para reporte.
     * Usado por el ReportService para saber qué imágenes incluir en el PDF.
     */
    @Query("SELECT ap FROM AnalysisPhoto ap " +
           "WHERE ap.footAnalysis.id = :footAnalysisId AND ap.isSelected = true AND ap.deleted = false " +
           "ORDER BY ap.photoOrder ASC")
    List<AnalysisPhoto> findSelectedByFootAnalysisId(@Param("footAnalysisId") Long footAnalysisId);
}
