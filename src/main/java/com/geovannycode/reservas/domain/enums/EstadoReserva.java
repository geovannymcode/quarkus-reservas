package com.geovannycode.reservas.domain.enums;

/**
 * Estados posibles del ciclo de vida de una Reserva.
 */
public enum EstadoReserva {
    CREADA,
    CANCELADA,
    COMPLETADA;

    /** Retorna true si la reserva se considera activa (no cancelada ni completada). */
    public boolean esActiva() {
        return this == CREADA;
    }
}

