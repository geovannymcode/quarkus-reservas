package com.geovannycode.reservas.application.dto.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO de salida con los datos de un Profesional.
 * Usa Java Record para concisión e inmutabilidad.
 */
@Schema(description = "Información de un profesional registrado en el sistema")
public record ProfesionalResponse(

        @Schema(description = "Identificador único del profesional")
        UUID id,

        @Schema(description = "Nombres del profesional")
        String nombres,

        @Schema(description = "Apellidos del profesional")
        String apellidos,

        @Schema(description = "Especialidad del profesional")
        String especialidad,

        @Schema(description = "Indica si el profesional está activo")
        boolean estadoActivo
) {
}
