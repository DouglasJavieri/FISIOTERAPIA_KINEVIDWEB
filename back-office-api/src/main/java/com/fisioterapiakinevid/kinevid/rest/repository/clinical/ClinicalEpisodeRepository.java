package com.fisioterapiakinevid.kinevid.rest.repository.clinical;

import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.ClinicalEpisode;
import com.fisioterapiakinevid.kinevid.rest.model.enums.clinical.EpisodeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 23/04/2026
 */
@Repository
public interface ClinicalEpisodeRepository extends JpaRepository<ClinicalEpisode, Long> {

    @Query("SELECT e FROM ClinicalEpisode e WHERE e.patient.id = :patientId AND e.deleted = false ORDER BY e.episodeNumber DESC")
    Page<ClinicalEpisode> findAllByPatientId(@Param("patientId") Long patientId, Pageable pageable);

    @Query("SELECT e FROM ClinicalEpisode e WHERE e.deleted = false " +
           "AND (:patientId IS NULL OR e.patient.id = :patientId) " +
           "AND (:status IS NULL OR e.episodeStatus = :status)")
    Page<ClinicalEpisode> findAllWithFilters(
            @Param("patientId") Long patientId,
            @Param("status") EpisodeStatus status,
            Pageable pageable);

    @Query("SELECT e FROM ClinicalEpisode e WHERE e.patient.id = :patientId AND e.episodeStatus = :status AND e.deleted = false")
    Optional<ClinicalEpisode> findActiveEpisodeByPatientId(@Param("patientId") Long patientId, @Param("status") EpisodeStatus status);

    @Query("SELECT MAX(e.episodeNumber) FROM ClinicalEpisode e WHERE e.patient.id = :patientId AND e.deleted = false")
    Optional<Integer> findMaxEpisodeNumberByPatientId(@Param("patientId") Long patientId);

    boolean existsByPatientIdAndEpisodeNumberAndDeletedFalse(Long patientId, Integer episodeNumber);
}


