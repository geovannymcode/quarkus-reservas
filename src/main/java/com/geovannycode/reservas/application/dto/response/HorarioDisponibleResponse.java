package com.geovannycode.reservas.application.dto.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO de salida con los datos de un HorarioDisponible.
 * Usa Java Record para concisión e inmutabilidad.
 */
@Schema(description = "Información de un horario disponible registrado")
public record HorarioDisponibleResponse(

        @Schema(description = "Identificador único del horario")
        UUID id,

        @Schema(description = "Información resumida del profesional")
        ProfesionalResponse profesional,

        @Schema(description = "Fecha del bloque de disponibilidad", example = "2025-11-10")
        LocalDate fecha,

        @Schema(description = "Hora de inicio del bloque", example = "09:00")
        LocalTime horaInicio,

        @Schema(description = "Hora de fin del bloque", example = "12:00")
        LocalTime horaFin,

        @Schema(description = "true = disponible, false = ocupado")
        boolean estado
) {
}
