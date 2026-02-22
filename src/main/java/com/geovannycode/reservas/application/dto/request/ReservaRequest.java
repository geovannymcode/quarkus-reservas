package com.geovannycode.reservas.application.dto.request;


import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO de entrada para crear una reserva entre un cliente y un profesional.
 * Usa Java Record para inmutabilidad y concisión (Java 16+).
 */
@Schema(description = "Datos para crear una nueva reserva")
public record ReservaRequest(

        @NotNull(message = "La fecha de la reserva es obligatoria")
        @Schema(description = "Fecha de la reserva", example = "2025-11-10")
        LocalDate fecha,

        @NotNull(message = "La hora de inicio es obligatoria")
        @Schema(description = "Hora de inicio de la sesión", example = "10:00")
        LocalTime horaInicio,

        @NotNull(message = "La hora de fin es obligatoria")
        @Schema(description = "Hora de fin de la sesión", example = "11:00")
        LocalTime horaFin,

        @NotNull(message = "El ID del cliente es obligatorio")
        @Schema(description = "UUID del cliente que realiza la reserva")
        UUID clienteId,

        @NotNull(message = "El ID del profesional es obligatorio")
        @Schema(description = "UUID del profesional con quien se reserva")
        UUID profesionalId
) {
}