package com.fisioterapiakinevid.kinevid.rest.repository.emp;

import com.fisioterapiakinevid.kinevid.rest.model.entity.emp.Employee;
import com.fisioterapiakinevid.kinevid.rest.model.enums.emp.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 07/04/2026
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e " +
            "FROM Employee e " +
            "WHERE e.deleted = false " +
            "AND e.status <> com.fisioterapiakinevid.kinevid.rest.model.enums.emp.EmployeeStatus.ELIMINATION")
    Page<Employee> findAllActive(Pageable pageable);

    @Query("SELECT e " +
            "FROM Employee e " +
            "WHERE e.deleted = false " +
            "AND e.status = :status")
    Page<Employee> findAllByStatus(@Param("status") EmployeeStatus status, Pageable pageable);

    @Query("SELECT COUNT(e) > 0 " +
            "FROM Employee e " +
            "WHERE e.user.id = :userId AND e.deleted = false")
    boolean existsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(e) > 0 " +
            "FROM Employee e " +
            "WHERE e.user.id = :userId AND e.id <> :excludeId AND e.deleted = false")
    boolean existsByUserIdExcludingId(@Param("userId") Long userId, @Param("excludeId") Long excludeId);

    Optional<Employee> findById(Long id);
}




