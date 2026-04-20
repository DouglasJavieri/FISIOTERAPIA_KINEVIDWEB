package com.fisioterapiakinevid.kinevid.config.security.exception;

/**
 * @author Douglas Cristhian Javieri Vino
 * @created 27/03/2026
 */
public class JwtException extends RuntimeException {
    private String errorCode;
    public JwtException(String message) {
        super(message);
    }

    public JwtException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public JwtException(String message, Throwable cause) {super(message, cause);}

    public JwtException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    public String getErrorCode() {
        return errorCode;
    }
}
