package com.fisioterapiakinevid.kinevid.rest.model.dto.u;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 01/04/2026
 * DTO para actualización de usuario.
 */
@Data
public class UserUpdateRequestDTO {

    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 3, max = 30, message = "El nombre de usuario debe tener entre 3 y 30 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "El nombre de usuario solo puede contener letras, números y guiones bajos")
    private String username;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es válido")
    @Size(max = 50, message = "El email no puede superar los 50 caracteres")
    private String email;

    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    private String password;
}

