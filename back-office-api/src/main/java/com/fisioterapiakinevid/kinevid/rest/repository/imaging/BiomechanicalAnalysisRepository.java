package com.fisioterapiakinevid.kinevid.rest.repository.imaging;

import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.BiomechanicalAnalysis;
import com.fisioterapiakinevid.kinevid.rest.model.enums.imaging.FootSide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad BiomechanicalAnalysis.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Repository
public interface BiomechanicalAnalysisRepository extends JpaRepository<BiomechanicalAnalysis, Long> {

    /**
     * Busca un análisis biomecánico por ID con su análisis de pisada y sesión cargados.
     * Útil para validar pertenencia al análisis y verificar estado de la sesión.
     */
    @Query("SELECT ba FROM BiomechanicalAnalysis ba " +
           "JOIN FETCH ba.footAnalysis fa " +
           "JOIN FETCH fa.clinicalSession cs " +
           "WHERE ba.id = :id AND ba.deleted = false")
    Optional<BiomechanicalAnalysis> findByIdWithRelations(@Param("id") Long id);

    /**
     * Lista los análisis biomecánicos activos de un análisis de pisada.
     * Devuelve máximo 2 registros (LEFT y RIGHT), ordenados por foot_side.
     */
    @Query("SELECT ba FROM BiomechanicalAnalysis ba " +
           "WHERE ba.footAnalysis.id = :footAnalysisId AND ba.deleted = false " +
           "ORDER BY ba.footSide ASC")
    List<BiomechanicalAnalysis> findAllByFootAnalysisId(@Param("footAnalysisId") Long footAnalysisId);

    /**
     * Busca el análisis biomecánico de un pie específico dentro de un análisis.
     * Incluye registros eliminados para soportar la lógica de reactivación (upsert).
     */
    @Query("SELECT ba FROM BiomechanicalAnalysis ba " +
           "WHERE ba.footAnalysis.id = :footAnalysisId AND ba.footSide = :footSide")
    Optional<BiomechanicalAnalysis> findByFootAnalysisIdAndFootSideIncludingDeleted(
            @Param("footAnalysisId") Long footAnalysisId,
            @Param("footSide") FootSide footSide);
}
