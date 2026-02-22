package com.geovannycode.reservas.domain.exception;

import jakarta.ws.rs.core.Response;

/**
 * Excepción base para todas las excepciones de negocio de la aplicación.
 * Encapsula el código HTTP que debe retornarse al cliente REST.
 */
public class BusinessException extends RuntimeException {

    private final int httpStatusCode;

    public BusinessException(String message, Response.Status httpStatus) {
        super(message);
        this.httpStatusCode = httpStatus.getStatusCode();
    }

    public BusinessException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
