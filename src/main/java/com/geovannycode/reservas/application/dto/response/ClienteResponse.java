package com.geovannycode.reservas.application.dto.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO de salida con los datos de un Cliente.
 * Usa Java Record para concisión e inmutabilidad.
 */
@Schema(description = "Información de un cliente registrado en el sistema")
public record ClienteResponse(

        @Schema(description = "Identificador único del cliente")
        UUID id,

        @Schema(description = "Nombres del cliente")
        String nombres,

        @Schema(description = "Apellidos del cliente")
        String apellidos,

        @Schema(description = "Email único del cliente")
        String email,

        @Schema(description = "Teléfono de contacto del cliente")
        String telefono,

        @Schema(description = "Indica si el cliente está activo")
        boolean estadoActivo
) {
}

