package com.fisioterapiakinevid.kinevid.rest.repository.imaging;

import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.FootprintAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad FootprintAnalysis (Valoración Hernández Corvo).
 * Relación 1:1 con FootAnalysis — máximo un registro activo por análisis.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Repository
public interface FootprintAnalysisRepository extends JpaRepository<FootprintAnalysis, Long> {

    /**
     * Busca la valoración por ID con su análisis de pisada y sesión cargados.
     * Útil para validar pertenencia y verificar estado de la sesión.
     */
    @Query("SELECT fp FROM FootprintAnalysis fp " +
           "JOIN FETCH fp.footAnalysis fa " +
           "JOIN FETCH fa.clinicalSession cs " +
           "WHERE fp.id = :id AND fp.deleted = false")
    Optional<FootprintAnalysis> findByIdWithRelations(@Param("id") Long id);

    /**
     * Busca la valoración activa de un análisis de pisada.
     * Usado para obtener la valoración ya guardada.
     */
    @Query("SELECT fp FROM FootprintAnalysis fp " +
           "WHERE fp.footAnalysis.id = :footAnalysisId AND fp.deleted = false")
    Optional<FootprintAnalysis> findByFootAnalysisId(@Param("footAnalysisId") Long footAnalysisId);

    /**
     * Busca la valoración de un análisis incluyendo registros eliminados.
     * Usado en la lógica de upsert para reactivar si fue eliminada previamente.
     */
    @Query("SELECT fp FROM FootprintAnalysis fp " +
           "WHERE fp.footAnalysis.id = :footAnalysisId")
    Optional<FootprintAnalysis> findByFootAnalysisIdIncludingDeleted(@Param("footAnalysisId") Long footAnalysisId);
}
