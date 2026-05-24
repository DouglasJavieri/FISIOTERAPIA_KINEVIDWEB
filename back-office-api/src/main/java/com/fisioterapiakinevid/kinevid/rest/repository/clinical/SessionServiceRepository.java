package com.fisioterapiakinevid.kinevid.rest.repository.clinical;

import com.fisioterapiakinevid.kinevid.rest.model.entity.clinical.SessionService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 24/04/2026
 */
@Repository
public interface SessionServiceRepository extends JpaRepository<SessionService, Long> {

    @Query("SELECT ss FROM SessionService ss " +
           "JOIN FETCH ss.medicalService ms " +
           "WHERE ss.session.id = :sessionId AND ss.deleted = false")
    List<SessionService> findAllBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT ss FROM SessionService ss " +
           "JOIN FETCH ss.medicalService " +
           "JOIN FETCH ss.session s " +
           "WHERE ss.id = :id AND ss.deleted = false")
    Optional<SessionService> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT ss FROM SessionService ss " +
           "WHERE ss.session.id = :sessionId AND ss.medicalService.id = :medicalServiceId")
    Optional<SessionService> findBySessionIdAndMedicalServiceIdIncludingDeleted(
            @Param("sessionId") Long sessionId,
            @Param("medicalServiceId") Long medicalServiceId);

    @Query("SELECT ss FROM SessionService ss " +
           "JOIN FETCH ss.medicalService " +
           "WHERE ss.session.id = :sessionId AND ss.medicalService.id = :medicalServiceId AND ss.deleted = false")
    Optional<SessionService> findBySessionIdAndMedicalServiceId(
            @Param("sessionId") Long sessionId,
            @Param("medicalServiceId") Long medicalServiceId);
}

