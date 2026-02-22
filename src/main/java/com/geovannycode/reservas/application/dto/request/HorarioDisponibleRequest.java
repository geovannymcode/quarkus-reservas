package com.geovannycode.reservas.application.dto.request;

import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO de entrada para registrar un bloque de disponibilidad de un profesional.
 * Usa Java Record para inmutabilidad y concisión (Java 16+).
 */
@Schema(description = "Datos para registrar la disponibilidad horaria de un profesional")
public record HorarioDisponibleRequest(

        @NotNull(message = "El ID del profesional es obligatorio")
        @Schema(description = "UUID del profesional al que pertenece el horario")
        UUID profesionalId,

        @NotNull(message = "La fecha es obligatoria")
        @Schema(description = "Fecha de disponibilidad", example = "2025-11-10")
        LocalDate fecha,

        @NotNull(message = "La hora de inicio es obligatoria")
        @Schema(description = "Hora de inicio del bloque disponible", example = "09:00")
        LocalTime horaInicio,

        @NotNull(message = "La hora de fin es obligatoria")
        @Schema(description = "Hora de fin del bloque disponible", example = "12:00")
        LocalTime horaFin
) {
}
