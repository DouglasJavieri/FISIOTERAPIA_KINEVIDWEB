package com.fisioterapiakinevid.kinevid.config.security.exception;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 27/03/2026
 */
public class JwtTokenExpiredException extends JwtException {
    public JwtTokenExpiredException(String message) {
        super(message, "JWT_EXPIRED");
    }

    public JwtTokenExpiredException(String message, Throwable cause) {
        super(message, cause, "JWT_EXPIRED");
    }
}
