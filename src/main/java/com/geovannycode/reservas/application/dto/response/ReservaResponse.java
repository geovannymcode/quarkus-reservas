package com.geovannycode.reservas.application.dto.response;

import com.geovannycode.reservas.domain.enums.EstadoReserva;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO de salida con los datos completos de una Reserva.
 * Usa Java Record para concisión e inmutabilidad.
 */
@Schema(description = "Información completa de una reserva")
public record ReservaResponse(

        @Schema(description = "Identificador único de la reserva")
        UUID id,

        @Schema(description = "Fecha de la sesión agendada", example = "2025-11-10")
        LocalDate fecha,

        @Schema(description = "Hora de inicio de la sesión", example = "10:00")
        LocalTime horaInicio,

        @Schema(description = "Hora de fin de la sesión", example = "11:00")
        LocalTime horaFin,

        @Schema(description = "Información del cliente")
        ClienteResponse cliente,

        @Schema(description = "Información del profesional")
        ProfesionalResponse profesional,

        @Schema(description = "Estado actual de la reserva: CREADA, CANCELADA, COMPLETADA")
        EstadoReserva estado
) {
}
