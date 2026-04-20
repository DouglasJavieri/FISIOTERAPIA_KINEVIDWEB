package com.fisioterapiakinevid.kinevid.rest.model.dto.pat;

import com.fisioterapiakinevid.kinevid.rest.model.enums.pat.BloodType;
import com.fisioterapiakinevid.kinevid.rest.model.enums.pat.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 15/04/2026
 */
@Data
public class PatientUpdateRequestDTO {

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido paterno es requerido")
    @Size(max = 80, message = "El apellido paterno no puede superar los 80 caracteres")
    private String paternalSurname;

    @Size(max = 80, message = "El apellido materno no puede superar los 80 caracteres")
    private String maternalSurname;

    @NotBlank(message = "El carnet de identidad es requerido")
    @Size(max = 20, message = "El carnet no puede superar los 20 caracteres")
    private String ci;

    @NotNull(message = "El gÃ©nero es requerido")
    private Gender gender;

    @NotNull(message = "La fecha de nacimiento es requerida")
    private LocalDate birthDate;

    @Size(max = 15, message = "El telÃ©fono no puede superar los 15 caracteres")
    private String phone;

    @Email(message = "El formato del email no es vÃ¡lido")
    @Size(max = 80, message = "El email no puede superar los 80 caracteres")
    private String email;

    @Size(max = 200, message = "La direcciÃ³n no puede superar los 200 caracteres")
    private String address;

    private BloodType bloodType;

    @Size(max = 100, message = "La ocupaciÃ³n no puede superar los 100 caracteres")
    private String occupation;

    @Size(max = 100, message = "El nombre del contacto de emergencia no puede superar los 100 caracteres")
    private String emergencyContactName;

    @Size(max = 15, message = "El telÃ©fono del contacto de emergencia no puede superar los 15 caracteres")
    private String emergencyContactPhone;

    @Size(max = 500, message = "Las notas no pueden superar los 500 caracteres")
    private String notes;
}


