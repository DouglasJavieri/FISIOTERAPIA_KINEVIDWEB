package com.fisioterapiakinevid.kinevid.rest.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 27/03/2026
 * DTO para solicitud de renovación de Access Token
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequestDto {
    @NotBlank(message = "El Refresh Token es requerido")
    private String refreshToken;
}