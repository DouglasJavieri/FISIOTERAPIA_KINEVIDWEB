package com.fisioterapiakinevid.kinevid.rest.repository.imaging;

import com.fisioterapiakinevid.kinevid.rest.model.entity.imaging.FootAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad FootAnalysis.
 * Consultas principales: por sesión clínica y por ID con relaciones.
 *
 * @author Douglas Cristhian Javieri Vino
 * @created 14/05/2026
 */
@Repository
public interface FootAnalysisRepository extends JpaRepository<FootAnalysis, Long> {

    /**
     * Busca un análisis de pisada por ID incluyendo la sesión, episodio, paciente y empleado.
     * Útil para construir respuestas completas y para generación de reportes PDF.
     */
    @Query("SELECT fa FROM FootAnalysis fa " +
           "JOIN FETCH fa.clinicalSession cs " +
           "JOIN FETCH cs.episode e " +
           "JOIN FETCH e.patient " +
           "JOIN FETCH cs.employee " +
           "WHERE fa.id = :id AND fa.deleted = false")
    Optional<FootAnalysis> findByIdWithRelations(@Param("id") Long id);

    /**
     * Busca el análisis de pisada asociado a una sesión clínica.
     * Relación 1:1 — devuelve máximo un resultado.
     */
    @Query("SELECT fa FROM FootAnalysis fa " +
           "JOIN FETCH fa.clinicalSession cs " +
           "JOIN FETCH cs.episode e " +
           "JOIN FETCH e.patient " +
           "JOIN FETCH cs.employee " +
           "WHERE cs.id = :sessionId AND fa.deleted = false")
    Optional<FootAnalysis> findByClinicalSessionId(@Param("sessionId") Long sessionId);

    /**
     * Verifica si ya existe un análisis de pisada para una sesión (no eliminado).
     * Usado como validación antes de crear uno nuevo.
     */
    boolean existsByClinicalSessionIdAndDeletedFalse(Long clinicalSessionId);
}
