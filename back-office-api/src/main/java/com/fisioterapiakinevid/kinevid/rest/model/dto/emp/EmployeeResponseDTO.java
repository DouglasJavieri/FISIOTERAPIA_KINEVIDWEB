package com.fisioterapiakinevid.kinevid.rest.model.dto.emp;

import com.fisioterapiakinevid.kinevid.rest.model.entity.emp.Employee;
import com.fisioterapiakinevid.kinevid.rest.model.enums.emp.BolivianDepartment;
import com.fisioterapiakinevid.kinevid.rest.model.enums.emp.EmployeeStatus;
import lombok.*;

import java.time.LocalDate;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 07/04/2026
 * DTO de respuesta para empleado.
 * Proyecta los campos del User asignado de forma plana (sin exponer la contraseÃ±a).
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {

    private Long id;
    private String firstName;
    private String paternalSurname;
    private String maternalSurname;
    private String ci;
    private BolivianDepartment expedition;
    private String specialty;
    private String phone;
    private String address;
    private BolivianDepartment department;
    private String professionalEmail;
    private LocalDate admissionDate;
    private EmployeeStatus status;

    private Long userId;
    private String username;
    private String email;

    public EmployeeResponseDTO(Employee employee) {
        this.id = employee.getId();
        this.firstName = employee.getFirstName();
        this.paternalSurname = employee.getPaternalSurname();
        this.maternalSurname = employee.getMaternalSurname();
        this.ci = employee.getCi();
        this.expedition = employee.getExpedition();
        this.specialty = employee.getSpecialty();
        this.phone = employee.getPhone();
        this.address = employee.getAddress();
        this.department = employee.getDepartment();
        this.professionalEmail = employee.getProfessionalEmail();
        this.admissionDate = employee.getAdmissionDate();
        this.status = employee.getStatus();

        if (employee.getUser() != null) {
            this.userId   = employee.getUser().getId();
            this.username = employee.getUser().getUsername();
            this.email    = employee.getUser().getEmail();
        }
    }
}

