package com.fisioterapiakinevid.kinevid.rest.repository.clinical;

import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.ClinicalSession;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * Creado: 23/04/2026
 */
@Repository
public interface ClinicalSessionRepository extends JpaRepository<ClinicalSession, Long> {

    @Query("SELECT s FROM ClinicalSession s " +
           "JOIN FETCH s.episode e " +
           "JOIN FETCH e.patient " +
           "JOIN FETCH s.employee " +
           "WHERE s.id = :sessionId AND s.deleted = false")
    Optional<ClinicalSession> findByIdWithRelations(@Param("sessionId") Long sessionId);

    @Query("SELECT s FROM ClinicalSession s WHERE s.episode.id = :episodeId AND s.deleted = false ORDER BY s.sessionNumber DESC")
    Page<ClinicalSession> findAllByEpisodeId(@Param("episodeId") Long episodeId, Pageable pageable);

    @Query("SELECT MAX(s.sessionNumber) FROM ClinicalSession s WHERE s.episode.id = :episodeId AND s.deleted = false")
    Optional<Integer> findMaxSessionNumberByEpisodeId(@Param("episodeId") Long episodeId);

    @Query("SELECT s FROM ClinicalSession s WHERE s.episode.id = :episodeId AND s.sessionStatus = :status AND s.deleted = false")
    Page<ClinicalSession> findAllByEpisodeIdAndStatus(@Param("episodeId") Long episodeId, @Param("status") SessionStatus status, Pageable pageable);
}

