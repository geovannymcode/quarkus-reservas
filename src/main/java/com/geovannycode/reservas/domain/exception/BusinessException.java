package com.geovannycode.reservas.domain.exception;

import jakarta.ws.rs.core.Response;

/**
 * Excepción base para todas las excepciones de negocio de la aplicación.
 * Encapsula el código HTTP que debe retornarse al cliente REST.
 */
public class BusinessException extends RuntimeException {

    private final Response.Status httpStatus;

    public BusinessException(String message, Response.Status httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public Response.Status getHttpStatus() {
        return httpStatus;
    }
}
