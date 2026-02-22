package com.geovannycode.reservas.domain.exception;

import jakarta.ws.rs.core.Response;

/**
 * Excepción lanzada cuando se intenta crear una Reserva que se solapa
 * con otra reserva activa del mismo profesional.
 * Mapea al código HTTP 409 Conflict.
 */
public class ReservaSolapamientoException extends BusinessException {

    public ReservaSolapamientoException(String message) {
        super(message, Response.Status.CONFLICT);
    }
}
