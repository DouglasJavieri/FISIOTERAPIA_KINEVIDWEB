package com.fisioterapiakinevid.kinevid.rest.repository.p;

import com.fisioterapiakinevid.kinevid.rest.model.dto.p.PagedPermissionResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.p.Permission;
import com.fisioterapiakinevid.kinevid.rest.model.enums.p.PermissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 03/03/2026
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Permission p " +
            "WHERE p.deleted = false " +
            "AND p.name = :name")
    boolean existsPermissionByName(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Permission p " +
            "WHERE p.deleted = false " +
            "AND p.name = :name AND p.id <> :excludeId")
    boolean existsPermissionByNameExcludingId(@Param("name") String name, @Param("excludeId") Long excludeId);

    @Query("SELECT p " +
            "FROM Permission p " +
            "WHERE p.deleted = false AND p.name = :name")
    Optional<Permission> findByName(@Param("name") String name);

    @Query("SELECT new com.fisioterapiakinevid.kinevid.rest.model.dto.p.PagedPermissionResponseDto(p) " +
            "FROM Permission p " +
            "WHERE p.deleted = false AND (:status IS NULL OR p.status = :status)")
    Page<PagedPermissionResponseDto> findAllPermissions(@Param("status") PermissionStatus status,
                                                        Pageable pageable);
}

