package com.geovannycode.reservas.domain.exception;

import jakarta.ws.rs.core.Response;

/**
 * Excepción lanzada cuando no existe un HorarioDisponible que cubra
 * el intervalo de tiempo solicitado en una reserva.
 * Mapea al código HTTP 400 Bad Request.
 */
public class HorarioNoDisponibleException extends BusinessException {

    public HorarioNoDisponibleException(String message) {
        super(message, Response.Status.BAD_REQUEST);
    }
}
