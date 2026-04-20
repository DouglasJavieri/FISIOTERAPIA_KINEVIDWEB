package com.fisioterapiakinevid.kinevid.rest.repository.pat;

import com.fisioterapiakinevid.kinevid.rest.model.entity.pat.Patient;
import com.fisioterapiakinevid.kinevid.rest.model.enums.pat.PatientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("SELECT p " +
            "FROM Patient p " +
            "WHERE p.deleted = false AND p.status <> com.fisioterapiakinevid.kinevid.rest.model.enums.pat.PatientStatus.ELIMINATION")
    Page<Patient> findAllActive(Pageable pageable);

    @Query("SELECT p " +
            "FROM Patient p " +
            "WHERE p.deleted = false AND p.status = :status")
    Page<Patient> findAllByStatus(@Param("status") PatientStatus status, Pageable pageable);

    @Query("SELECT p " +
            "FROM Patient p " +
            "WHERE p.deleted = false " +
           "AND p.status <> com.fisioterapiakinevid.kinevid.rest.model.enums.pat.PatientStatus.ELIMINATION " +
           "AND (LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.paternalSurname) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.maternalSurname) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR p.ci LIKE CONCAT('%', :search, '%'))")
    Page<Patient> searchByNameOrCi(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(p) > 0 FROM Patient p WHERE p.ci = :ci AND p.deleted = false")
    boolean existsByCi(@Param("ci") String ci);

    @Query("SELECT COUNT(p) > 0 FROM Patient p WHERE p.ci = :ci AND p.id <> :excludeId AND p.deleted = false")
    boolean existsByCiExcludingId(@Param("ci") String ci, @Param("excludeId") Long excludeId);

    @Query("SELECT p " +
            "FROM Patient p " +
            "WHERE p.deleted = false " +
           "AND p.status = com.fisioterapiakinevid.kinevid.rest.model.enums.pat.PatientStatus.ACTIVE " +
           "ORDER BY p.paternalSurname, p.firstName")
    List<Patient> findAllActiveList();
}


