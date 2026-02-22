package com.geovannycode.reservas.application.dto.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO de salida para el ranking de profesionales por número de reservas activas.
 * Usa Java Record para concisión e inmutabilidad.
 */
@Schema(description = "Profesional con conteo de reservas activas (para ranking)")
public record ProfesionalConReservasResponse(

        @Schema(description = "Información del profesional")
        ProfesionalResponse profesional,

        @Schema(description = "Total de reservas activas del profesional", example = "5")
        long totalReservasActivas
) {
}
