package com.fisioterapiakinevid.kinevid.rest.repository.u;

import com.fisioterapiakinevid.kinevid.rest.model.dto.u.EmailResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.dto.u.UsernameResponseDto;
import com.fisioterapiakinevid.kinevid.rest.model.entity.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/02/2026
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE LOWER(u.username) = :username AND u.deleted = false ")
    Optional<User> findByUsernameAuthentication(@Param("username") String username);

    @Query("SELECT new com.fisioterapiakinevid.kinevid.rest.model.dto.u.EmailResponseDto(u.email) " +
            "FROM User u " +
            "WHERE u.deleted = false " +
            "AND u.status <> com.fisioterapiakinevid.kinevid.rest.model.enums.auth.UserStatus.ELIMINATION " +
            "AND u.email = :email ")
    Optional<EmailResponseDto> findByEmail (@Param("email") String email);

    @Query("SELECT new com.fisioterapiakinevid.kinevid.rest.model.dto.u.UsernameResponseDto(u.username) " +
            "FROM User u " +
            "WHERE u.deleted = false " +
            "AND u.status <> com.fisioterapiakinevid.kinevid.rest.model.enums.auth.UserStatus.ELIMINATION " +
            "AND u.username = :username ")
    Optional<UsernameResponseDto> findByUsername(@Param("username") String username);

    /**
     * Busca username duplicado excluyendo al propio usuario (para operaciones de actualizaciÃ³n)
     */
    @Query("SELECT new com.fisioterapiakinevid.kinevid.rest.model.dto.u.UsernameResponseDto(u.username) " +
            "FROM User u " +
            "WHERE u.deleted = false " +
            "AND u.status <> com.fisioterapiakinevid.kinevid.rest.model.enums.auth.UserStatus.ELIMINATION " +
            "AND u.username = :username " +
            "AND u.id <> :excludeId")
    Optional<UsernameResponseDto> findByUsernameExcludingId(@Param("username") String username, @Param("excludeId") Long excludeId);

    /**
     * Busca email duplicado excluyendo al propio usuario (para operaciones de actualizaciÃ³n)
     */
    @Query("SELECT new com.fisioterapiakinevid.kinevid.rest.model.dto.u.EmailResponseDto(u.email) " +
            "FROM User u " +
            "WHERE u.deleted = false " +
            "AND u.status <> com.fisioterapiakinevid.kinevid.rest.model.enums.auth.UserStatus.ELIMINATION " +
            "AND u.email = :email " +
            "AND u.id <> :excludeId")
    Optional<EmailResponseDto> findByEmailExcludingId(@Param("email") String email, @Param("excludeId") Long excludeId);

    Optional<User> findById(Long id);

    @Query("SELECT u FROM User u " +
            "WHERE u.deleted = false " +
            "AND u.status <> com.fisioterapiakinevid.kinevid.rest.model.enums.auth.UserStatus.ELIMINATION")
    Page<User> findAllActive(Pageable pageable);

    @Query("SELECT ur.role.name " +
            "FROM UserRole ur " +
            "WHERE ur.user.id = :userId " +
            "AND ur.role.status = com.fisioterapiakinevid.kinevid.rest.model.enums.role.RoleStatus.ACTIVE " +
            "AND ur.role.deleted = false " +
            "AND ur.deleted = false")
    List<String> findRoleNamesByUserId(@Param("userId") Long userId);

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.deleted = false " +
            "AND u.status = com.fisioterapiakinevid.kinevid.rest.model.enums.auth.UserStatus.ACTIVE " +
            "AND u.id NOT IN (" +
            "  SELECT e.user.id FROM com.fisioterapiakinevid.kinevid.rest.model.entity.emp.Employee e " +
            "  WHERE e.user IS NOT NULL AND e.deleted = false" +
            ")")
    List<User> findUsersAvailableForEmployee();

    @Query("SELECT DISTINCT rp.permission.name " +
            "FROM UserRole ur, RolePermission rp " +
            "WHERE ur.user.id = :userId " +
            "AND rp.role.id = ur.role.id " +
            "AND ur.role.status = com.fisioterapiakinevid.kinevid.rest.model.enums.role.RoleStatus.ACTIVE " +
            "AND ur.role.deleted = false " +
            "AND ur.deleted = false " +
            "AND rp.deleted = false")
    List<String> findPermissionNamesByUserId(@Param("userId") Long userId);

}

