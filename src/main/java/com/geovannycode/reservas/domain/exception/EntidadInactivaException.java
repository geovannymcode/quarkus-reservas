package com.geovannycode.reservas.domain.exception;

/**
 * Excepción lanzada cuando se intenta operar con un Cliente o Profesional
 * que tiene estadoActivo = false.
 * Mapea al código HTTP 422 Unprocessable Entity.
 */
public class EntidadInactivaException extends BusinessException {

    public EntidadInactivaException(String message) {
        super(message, 422);
    }
}
