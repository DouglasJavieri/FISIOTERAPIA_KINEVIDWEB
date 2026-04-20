package com.fisioterapiakinevid.kinevid.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 27/03/2026
 * DTO para respuestas de error HTTP
 * Sigue el patrón de ResponseBody pero específico para errores
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * Código de error (ej: JWT_EXPIRED, JWT_INVALID, UNAUTHORIZED)
     */
    private String code;

    /**
     * Mensaje de error legible para el usuario/cliente
     */
    private String message;

    /**
     * Detalle técnico del error (opcional)
     */
    private String detail;

    /**
     * Timestamp del error
     */
    private LocalDateTime timestamp;

    /**
     * Path del endpoint que generó el error
     */
    private String path;

    /**
     * Status HTTP (ej: 401, 403, 400)
     */
    private Integer status;

    /**
     * Constructor simplificado para uso rápido
     */
    public ErrorResponse(String code, String message, Integer status) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor con detalle
     */
    public ErrorResponse(String code, String message, String detail, Integer status) {
        this.code = code;
        this.message = message;
        this.detail = detail;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}