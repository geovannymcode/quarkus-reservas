package com.geovannycode.reservas.domain.exception;

import jakarta.ws.rs.core.Response;

/**
 * Excepción lanzada cuando se intenta registrar un HorarioDisponible
 * que se solapa con uno existente para el mismo profesional.
 * Mapea al código HTTP 409 Conflict.
 */
public class HorarioSolapamientoException extends BusinessException {

    public HorarioSolapamientoException(String message) {
        super(message, Response.Status.CONFLICT);
    }
}