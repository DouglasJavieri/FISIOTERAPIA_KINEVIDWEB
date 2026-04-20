package com.fisioterapiakinevid.kinevid.rest.repository.ur;

import com.fisioterapiakinevid.kinevid.rest.model.entity.role.Role;
import com.fisioterapiakinevid.kinevid.rest.model.entity.ur.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 16/02/2026
 */
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT ur.role " +
            "FROM UserRole ur " +
            "WHERE ur.user.id = :idUser AND ur.deleted = false")
    List<Role> findRolesByUserId(@Param("idUser") Long idUser);

    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END " +
            "FROM UserRole ur " +
            "WHERE ur.user.id = :userId AND ur.role.id = :roleId AND ur.deleted = false")
    boolean existsByUserIdAndRoleId(@Param("userId") Long userId,
                                    @Param("roleId") Long roleId);

    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END " +
            "FROM UserRole ur " +
            "WHERE ur.role.id = :roleId AND ur.deleted = false")
    boolean hasActiveUsersByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT ur FROM UserRole ur " +
            "WHERE ur.user.id = :userId AND ur.role.id = :roleId AND ur.deleted = false")
    java.util.Optional<UserRole> findByUserIdAndRoleId(@Param("userId") Long userId,
                                                        @Param("roleId") Long roleId);
}

