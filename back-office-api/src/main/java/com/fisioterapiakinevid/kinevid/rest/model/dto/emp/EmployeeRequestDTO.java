package com.fisioterapiakinevid.kinevid.rest.model.dto.emp;

import com.fisioterapiakinevid.kinevid.rest.model.enums.emp.BolivianDepartment;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 07/04/2026
 * DTO para la creaciÃ³n de un empleado.
 */
@Data
public class EmployeeRequestDTO {

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido paterno es requerido")
    @Size(max = 80, message = "El apellido paterno no puede superar los 80 caracteres")
    private String paternalSurname;

    @Size(max = 80, message = "El apellido materno no puede superar los 80 caracteres")
    private String maternalSurname;

    @NotBlank(message = "El carnet es requerido")
    @Size(max = 20, message = "El carnet no puede superar los 20 caracteres")
    private String ci;

    @NotNull(message = "El lugar de expediciÃ³n es requerido")
    private BolivianDepartment expedition;

    @NotBlank(message = "La especialidad es requerida")
    @Size(max = 100, message = "La especialidad no puede superar los 100 caracteres")
    private String specialty;

    @Size(max = 15, message = "El telÃ©fono no puede superar los 15 caracteres")
    private String phone;

    @Size(max = 150, message = "La direcciÃ³n no puede superar los 150 caracteres")
    private String address;

    @NotNull(message = "El departamento es requerido")
    private BolivianDepartment department;

    @Email(message = "El formato del email profesional no es vÃ¡lido")
    @Size(max = 80, message = "El email profesional no puede superar los 80 caracteres")
    private String professionalEmail;

    @NotNull(message = "La fecha de admisiÃ³n es requerida")
    private LocalDate admissionDate;
}

