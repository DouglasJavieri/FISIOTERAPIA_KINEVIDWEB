package com.fisioterapiakinevid.kinevid.rest.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;

@Getter
public class ApiResponseException extends ResponseStatusException implements Serializable{

    private String title;
    private String message;


    public ApiResponseException(HttpStatus status, String title, String reason, String message) {
        super(status, reason);
        this.title = title;
        this.message = message;
    }

    public ApiResponseException(HttpStatus status, String title, String reason) {
        super(status, reason);
        this.title = title;
    }

    public ApiResponseException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public static ApiResponseException pruebaReport(String title, String reason, String message) {
        return new ApiResponseException(HttpStatus.CONFLICT, title, reason, message);
    }

    public static ApiResponseException badRequest(String title, String detail){
        return new ApiResponseException(HttpStatus.BAD_REQUEST, title, detail);
    }
    public static ApiResponseException badRequest(String detail){
        return new ApiResponseException(HttpStatus.BAD_REQUEST, detail);
    }
    public static ApiResponseException serverError(String title, String detail){
        return new ApiResponseException(HttpStatus.INTERNAL_SERVER_ERROR, title, detail);
    }
    public static ApiResponseException serverError(String detail){
        return new ApiResponseException(HttpStatus.INTERNAL_SERVER_ERROR, detail);
    }

    public static ApiResponseException notContent(String title, String detail){
        return new ApiResponseException(HttpStatus.NO_CONTENT, title, detail);
    }
    public static ApiResponseException notContent(String detail){
        return new ApiResponseException(HttpStatus.NO_CONTENT, detail);
    }

    public static ApiResponseException notFound(String title, String detail){
        return new ApiResponseException(HttpStatus.NOT_FOUND, title, detail);
    }
    public static ApiResponseException notFound(String detail){
        return new ApiResponseException(HttpStatus.NOT_FOUND, detail);
    }

    public static ApiResponseException conflict(String title, String detail){
        return new ApiResponseException(HttpStatus.CONFLICT, title, detail);
    }
    public static ApiResponseException conflict(String detail){
        return new ApiResponseException(HttpStatus.CONFLICT, detail);
    }

    public static ApiResponseException unauthorized(String detail){
        return new ApiResponseException(HttpStatus.UNAUTHORIZED, detail);
    }

}
