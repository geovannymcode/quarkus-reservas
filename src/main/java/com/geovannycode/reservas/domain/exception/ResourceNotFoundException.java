package com.geovannycode.reservas.domain.exception;

import jakarta.ws.rs.core.Response;

/**
 * Excepción lanzada cuando no se encuentra un recurso por su identificador.
 * Mapea al código HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(message, Response.Status.NOT_FOUND);
    }
}
