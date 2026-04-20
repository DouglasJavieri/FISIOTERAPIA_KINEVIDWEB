package com.fisioterapiakinevid.kinevid.rest.model.entity.emp;

import com.fisioterapiakinevid.kinevid.rest.model.base.AuditableEntity;
import com.fisioterapiakinevid.kinevid.rest.model.entity.auth.User;
import com.fisioterapiakinevid.kinevid.rest.model.enums.emp.BolivianDepartment;
import com.fisioterapiakinevid.kinevid.rest.model.enums.emp.EmployeeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 07/04/2026
 */
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee")
public class Employee extends AuditableEntity implements Serializable {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "SEQ_EMPLOYEE_ID_GENERATOR", sequenceName = "SEQ_EMPLOYEE_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EMPLOYEE_ID_GENERATOR")
    private Long id;

    @Basic
    @Column(name = "first_name", length = 80, nullable = false)
    private String firstName;

    @Basic
    @Column(name = "paternal_surname", length = 80, nullable = false)
    private String paternalSurname;

    @Basic
    @Column(name = "maternal_surname", length = 80)
    private String maternalSurname;

    @Basic
    @Column(name = "ci", length = 20, nullable = false)
    private String ci;

    @Enumerated(EnumType.STRING)
    @Column(name = "expedition", length = 30, nullable = false)
    private BolivianDepartment expedition;

    @Basic
    @Column(name = "specialty", length = 100, nullable = false)
    private String specialty;

    @Basic
    @Column(name = "phone", length = 15)
    private String phone;

    @Basic
    @Column(name = "address", length = 150)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "department", length = 30, nullable = false)
    private BolivianDepartment department;

    @Basic
    @Column(name = "professional_email", length = 80, unique = true)
    private String professionalEmail;

    @Column(name = "admission_date", nullable = false)
    private LocalDate admissionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_status", length = 30, nullable = false)
    private EmployeeStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;
}


