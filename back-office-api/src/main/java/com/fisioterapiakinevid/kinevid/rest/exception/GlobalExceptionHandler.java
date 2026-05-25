package com.fisioterapiakinevid.kinevid.rest.exception;

import com.fisioterapiakinevid.kinevid.rest.constants.ApiConstants;
import com.fisioterapiakinevid.kinevid.rest.response.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API.
 * Captura errores de validación y otros errores comunes para retornar respuestas consistentes.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Captura excepciones de validación de argumentos (@Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseBody<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Error de validación: {}", errors);

        ResponseBody<Map<String, String>> response = ResponseBody.<Map<String, String>>builder()
                .code(ApiConstants.BAD_REQUEST_CODE)
                .message("Error de validación en los datos enviados")
                .data(errors)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Captura excepciones personalizadas de la aplicación.
     */
    @ExceptionHandler(OperationException.class)
    public ResponseEntity<ResponseBody<Void>> handleOperationException(OperationException ex) {
        log.warn("Error operacional: {}", ex.getMessage());

        ResponseBody<Void> response = ResponseBody.<Void>builder()
                .code(ApiConstants.BAD_REQUEST_CODE)
                .message(ex.getMessage())
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Captura excepciones generales no manejadas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseBody<Void>> handleGeneralException(Exception ex) {
        log.error("Error no manejado: ", ex);

        ResponseBody<Void> response = ResponseBody.<Void>builder()
                .code(ApiConstants.INTERNAL_SERVER_ERROR_CODE)
                .message("Ocurrió un error inesperado en el servidor")
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
