package com.fisioterapiakinevid.kinevid.rest.repository.svc;

import com.fisioterapiakinevid.kinevid.rest.model.entity.svc.MedicalService;
import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceCategory;
import com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceStatus;
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
public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {

    @Query("SELECT s FROM MedicalService s WHERE s.deleted = false AND s.status <> com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceStatus.ELIMINATION")
    Page<MedicalService> findAllActive(Pageable pageable);

    @Query("SELECT s FROM MedicalService s WHERE s.deleted = false AND s.status = :status")
    Page<MedicalService> findAllByStatus(@Param("status") ServiceStatus status, Pageable pageable);

    @Query("SELECT s FROM MedicalService s WHERE s.deleted = false AND s.category = :category " +
           "AND s.status <> com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceStatus.ELIMINATION")
    Page<MedicalService> findAllByCategory(@Param("category") ServiceCategory category, Pageable pageable);

    @Query("SELECT COUNT(s) > 0 FROM MedicalService s WHERE LOWER(s.name) = LOWER(:name) AND s.deleted = false")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT COUNT(s) > 0 FROM MedicalService s WHERE LOWER(s.name) = LOWER(:name) AND s.id <> :excludeId AND s.deleted = false")
    boolean existsByNameExcludingId(@Param("name") String name, @Param("excludeId") Long excludeId);

    @Query("SELECT s FROM MedicalService s WHERE s.deleted = false " +
           "AND s.status = com.fisioterapiakinevid.kinevid.rest.model.enums.svc.ServiceStatus.ACTIVE " +
           "ORDER BY s.category, s.name")
    List<MedicalService> findAllActiveList();
}


