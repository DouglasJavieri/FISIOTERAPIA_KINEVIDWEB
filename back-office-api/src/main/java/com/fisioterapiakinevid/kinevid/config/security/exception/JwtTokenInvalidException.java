package com.fisioterapiakinevid.kinevid.config.security.exception;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 27/03/2026
 */
public class JwtTokenInvalidException extends JwtException {
    public JwtTokenInvalidException(String message) {
        super(message, "JWT_INVALID");
    }
    public JwtTokenInvalidException(String message, Throwable cause) {
        super(message, cause, "JWT_INVALID");
    }
}
