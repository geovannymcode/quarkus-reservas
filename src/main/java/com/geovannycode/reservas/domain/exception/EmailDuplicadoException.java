package com.geovannycode.reservas.domain.exception;

import jakarta.ws.rs.core.Response;

/**
 * Excepción lanzada cuando se intenta registrar un Cliente con un email
 * que ya existe en el sistema (restricción de unicidad).
 * Mapea al código HTTP 409 Conflict.
 */
public class EmailDuplicadoException extends BusinessException {

    public EmailDuplicadoException(String email) {
        super(String.format("Ya existe un cliente registrado con el email: %s", email), Response.Status.CONFLICT);
    }
}
