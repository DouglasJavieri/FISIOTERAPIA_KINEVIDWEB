package com.fisioterapiakinevid.kinevid.rest.util;

import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 27/03/2026
 * Utilidad para construir respuestas de autenticaciÃ³n y autorizaciÃ³n
 */
public class AuthResponseUtil {

    /**
     * Respuesta para token expirado
     */
    public static ResponseBody<Object> tokenExpired() {
        return ResponseBody.builder()
                .code("401")
                .message("Access Token expirado. Use el Refresh Token para obtener uno nuevo.")
                .data(null)
                .build();
    }

    /**
     * Respuesta para token invÃ¡lido
     */
    public static ResponseBody<Object> tokenInvalid() {
        return ResponseBody.builder()
                .code("401")
                .message("Access Token invÃ¡lido o malformado.")
                .data(null)
                .build();
    }

    /**
     * Respuesta para usuario no encontrado
     */
    public static ResponseBody<Object> userNotFound(String username) {
        return ResponseBody.builder()
                .code("401")
                .message("Usuario '" + username + "' no encontrado.")
                .data(null)
                .build();
    }

    /**
     * Respuesta para acceso denegado
     */
    public static ResponseBody<Object> accessDenied() {
        return ResponseBody.builder()
                .code("403")
                .message("Acceso denegado. No tiene permisos para acceder a este recurso.")
                .data(null)
                .build();
    }

    /**
     * Respuesta para error interno
     */
    public static ResponseBody<Object> internalServerError() {
        return ResponseBody.builder()
                .code("500")
                .message("Error interno del servidor. Por favor, contacte al administrador.")
                .data(null)
                .build();
    }

    /**
     * Respuesta genÃ©rica de error
     */
    public static ResponseBody<Object> error(String code, String message) {
        return ResponseBody.builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}
