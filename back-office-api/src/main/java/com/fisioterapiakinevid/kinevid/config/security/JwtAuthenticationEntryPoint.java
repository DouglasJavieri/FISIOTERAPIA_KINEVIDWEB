package com.fisioterapiakinevid.kinevid.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fisioterapiakinevid.kinevid.rest.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 27/03/2026
 * Entry Point para manejar excepciones de autenticaciÃ³n
 * Retorna respuestas JSON estructuradas en lugar de HTML
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.error("Error de autenticaciÃ³n: {}", authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("UNAUTHORIZED")
                .message("Acceso denegado: Token ausente o invÃ¡lido")
                .detail(authException.getMessage())
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .path(request.getServletPath())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
