package com.fisioterapiakinevid.kinevid.rest.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 16/02/2026
 * DTO para solicitud de login
 * Contiene credenciales del usuario (username + password)
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LoginRequestDto {

    @NotBlank(message = "El nombre de usuario es requerido")
    private String username;

    @NotBlank(message = "La contraseña es requerida")
    private String password;
}