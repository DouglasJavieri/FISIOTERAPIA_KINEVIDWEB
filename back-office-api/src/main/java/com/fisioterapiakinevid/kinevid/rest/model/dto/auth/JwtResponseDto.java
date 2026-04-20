package com.fisioterapiakinevid.kinevid.rest.model.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 16/02/2026
 * DTO para respuesta de autenticación
 * Contiene los tokens JWT (Access y Refresh) e información del usuario
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtResponseDto {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long expiresIn;

    private UserAuthInfoDto userInfo;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserAuthInfoDto {

        private Long id;

        private String username;

        private String email;

        private String fullName;

        private String role;

        private List<String> permissions;
    }
}