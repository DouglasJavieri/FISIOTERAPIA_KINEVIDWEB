package com.fisioterapiakinevid.kinevid.rest.repository.role;

import com.fisioterapiakinevid.kinevid.rest.model.dto.role.PagedRoleResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.role.Role;
import com.fisioterapiakinevid.kinevid.rest.model.enums.role.RoleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 02/03/2026
 */
@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Role r " +
            "WHERE r.deleted = false AND r.status <> com.fisioterapiakinevid.kinevid.rest.model.enums.role.RoleStatus.ELIMINATION " +
            "AND r.name = :name")
    boolean existsRoleByName(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Role r " +
            "WHERE r.deleted = false AND r.status <> com.fisioterapiakinevid.kinevid.rest.model.enums.role.RoleStatus.ELIMINATION " +
            "AND r.name = :name AND r.id <> :excludeId")
    boolean existsRoleByNameExcludingId(@Param("name") String name, @Param("excludeId") Long excludeId);

    @Query("SELECT new com.fisioterapiakinevid.kinevid.rest.model.dto.role.PagedRoleResponseDto(r) " +
            "FROM Role r " +
            "WHERE r.deleted = false AND (:status IS NULL OR r.status = :status) ")
    Page<PagedRoleResponseDto> findAllRoles(@Param("status") RoleStatus status,
                                            Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Role r " +
            "WHERE r.deleted = false AND r.status <> com.fisioterapiakinevid.kinevid.rest.model.enums.role.RoleStatus.ELIMINATION " +
            "AND r.id = :idRol")
    boolean existsRoleById(@Param("idRol") Long idRol);

    @Query("SELECT r " +
            "FROM Role r " +
            "WHERE r.deleted = false AND r.name = :name")
    Optional<Role> findByName(@Param("name") String name);

}

