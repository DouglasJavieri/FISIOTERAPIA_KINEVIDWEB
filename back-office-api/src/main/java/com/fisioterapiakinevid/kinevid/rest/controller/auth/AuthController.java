package com.fisioterapiakinevid.kinevid.rest.controller.auth;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.exception.ApiResponseException;
import com.fisioterapiakinevid.kinevid.rest.exception.OperationException;
import com.fisioterapiakinevid.kinevid.rest.model.dto.auth.*;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import com.fisioterapiakinevid.kinevid.rest.service.auth.AuthService;
import com.fisioterapiakinevid.kinevid.rest.util.ApiUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 16/02/2026
 * Controlador de autenticaciÃ³n
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "auth", description = "Endpoints de autenticaciÃ³n y autorizaciÃ³n")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "AutenticaciÃ³n de usuario (Login)",
            description = "Autentica un usuario con username y password. " +
                    "Retorna Access Token (JWT corta duraciÃ³n) y Refresh Token (JWT larga duraciÃ³n).",
            tags = {"auth"},
            responses = {
                    @ApiResponse(description = "AutenticaciÃ³n exitosa", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Error en validaciÃ³n de datos (username o password vacÃ­os)", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", description = "Credenciales invÃ¡lidas (usuario no existe o contraseÃ±a incorrecta)", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(hidden = true)))
            }, security = {})
    public ResponseEntity<ResponseBody<JwtResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            JwtResponseDto jwtResponse = authService.login(loginRequest);

            ResponseBody<JwtResponseDto> response = ApiUtil.buildResponseWithDefaults(jwtResponse);
            response.setMessage("Login exitoso. Tokens generados.");

            log.info("Login exitoso para usuario: {}", loginRequest.getUsername());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } catch (OperationException e) {
            log.warn("Error operacional en login para usuario: {} - {}",
                    loginRequest.getUsername(), e.getMessage());
            throw ApiResponseException.unauthorized(e.getMessage());

        } catch (Exception e) {
            log.error("Error inesperado en login para usuario: {}", loginRequest.getUsername(), e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/refresh")
    @Operation(
            summary = "Renovar Access Token",
            description = "Usa el Refresh Token para obtener un nuevo Access Token. " +
                    "Requiere un Access Token vÃ¡lido en el header Authorization. " +
                    "El Refresh Token debe estar vÃ¡lido (no revocado, no expirado).",
            tags = {"auth"},
            responses = {
                    @ApiResponse(description = "Access Token renovado exitosamente", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Error en validaciÃ³n de datos (Refresh Token vacÃ­o)", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", description = "Refresh Token invÃ¡lido, expirado o revocado", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<JwtResponseDto>> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequest) {
        try {
            JwtResponseDto jwtResponse = authService.refreshAccessToken(refreshTokenRequest);

            ResponseBody<JwtResponseDto> response = ApiUtil.buildResponseWithDefaults(jwtResponse);
            response.setMessage("Access Token renovado exitosamente.");

            log.info("Access Token renovado exitosamente");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } catch (OperationException e) {
            log.warn("Error operacional en refresh token: {}", e.getMessage());
            throw ApiResponseException.unauthorized(e.getMessage());

        } catch (Exception e) {
            log.error("Error inesperado en refresh token", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Cerrar sesiÃ³n (Logout)",
            description = "Revoca el Refresh Token, invalidando el acceso hasta un nuevo login. " +
                    "Requiere un Access Token vÃ¡lido en el header Authorization.",
            tags = {"auth"},
            responses = {
                    @ApiResponse(description = "Logout exitoso", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseBody.class))),
                    @ApiResponse(responseCode = "400", description = "Error en validaciÃ³n de datos (Refresh Token vacÃ­o)", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "401", description = "No autenticado o Access Token invÃ¡lido", content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(hidden = true)))
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Void>> logout(@Valid @RequestBody LogoutRequestDto logoutRequest) {
        try {
            authService.logout(logoutRequest.getRefreshToken());
            ResponseBody<Void> response = ResponseBody.<Void>builder()
                    .code(ApiConstants.OK_CODE)
                    .message("Logout exitoso. SesiÃ³n cerrada.")
                    .data(null)
                    .build();

            log.info("Logout exitoso - Token revocado");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } catch (OperationException e) {
            log.warn("Error operacional en logout: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());

        } catch (Exception e) {
            log.error("Error inesperado en logout", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/change-password")
    @Operation(
            summary = "Cambiar contraseÃ±a",
            description = "Cambia la contraseÃ±a del usuario autenticado. Revoca todos los tokens.",
            tags = {"auth"},
            responses = {
                    @ApiResponse(description = "Ã‰xito", responseCode = "200", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "ValidaciÃ³n fallida", content = @Content),
                    @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno", content = @Content)
            }, security = @SecurityRequirement(name = "bearerToken"))
    public ResponseEntity<ResponseBody<Void>> changePassword(@Valid @RequestBody ChangePasswordRequestDto changePasswordRequest) {
        try {
            authService.changePassword(changePasswordRequest);
            ResponseBody<Void> response = ResponseBody.<Void>builder()
                    .code(ApiConstants.OK_CODE)
                    .message("ContraseÃ±a cambiada. Inicie sesiÃ³n nuevamente.")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (OperationException e) {
            log.warn("Error cambiar contraseÃ±a: {}", e.getMessage());
            throw ApiResponseException.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado cambiar contraseÃ±a", e);
            throw ApiResponseException.serverError(ApiConstants.INTERNAL_SERVER_ERROR);
        }
    }

}
