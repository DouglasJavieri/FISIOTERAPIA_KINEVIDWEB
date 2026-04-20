package com.fisioterapiakinevid.kinevid.rest.repository.rp;

import com.fisioterapiakinevid.kinevid.rest.model.entity.p.Permission;
import com.fisioterapiakinevid.kinevid.rest.model.entity.rp.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 16/02/2026
 */
public interface RolePermissionRepository extends JpaRepository<RolePermission,Long> {

    @Query("SELECT rp.permission " +
            "FROM RolePermission rp " +
            "WHERE rp.role.id = :idRole AND rp.deleted = false")
    List<Permission> findPermissionsByRoleId(@Param("idRole") Long idRole);

    @Query("SELECT CASE WHEN COUNT(rp) > 0 THEN true ELSE false END " +
            "FROM RolePermission rp " +
            "WHERE rp.role.id = :roleId AND rp.permission.id = :permissionId AND rp.deleted = false")
    boolean existsByRoleIdAndPermissionId(@Param("roleId") Long roleId,
                                          @Param("permissionId") Long permissionId);

    @Query("SELECT rp " +
            "FROM RolePermission rp " +
            "WHERE rp.role.id = :roleId AND rp.permission.id = :permissionId AND rp.deleted = false")
    Optional<RolePermission> findByRoleIdAndPermissionId(@Param("roleId") Long roleId,
                                                         @Param("permissionId") Long permissionId);

    @Query("SELECT CASE WHEN COUNT(rp) > 0 THEN true ELSE false END " +
            "FROM RolePermission rp " +
            "WHERE rp.permission.id = :permissionId AND rp.deleted = false")
    boolean hasActiveRolesByPermissionId(@Param("permissionId") Long permissionId);

    @Query("SELECT DISTINCT rp.permission.name " +
            "FROM RolePermission rp " +
            "WHERE rp.deleted = false " +
            "  AND rp.role.id IN (" +
            "      SELECT ur.role.id FROM UserRole ur " +
            "      WHERE ur.user.id = :userId AND ur.deleted = false" +
            "  )")
    List<String> findPermissionNamesByUserId(@Param("userId") Long userId);

}

