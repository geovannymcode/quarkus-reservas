package com.geovannycode.reservas.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO de salida estándar para respuestas de error de la API.
 * Usa Java Record para concisión e inmutabilidad.
 */
@Schema(description = "Respuesta de error estándar de la API")
public record ErrorResponse(

        @Schema(description = "Código HTTP del error", example = "404")
        int codigo,

        @Schema(description = "Mensaje descriptivo del error", example = "Profesional no encontrado")
        String mensaje,

        @Schema(description = "Ruta del endpoint que generó el error", example = "/api/profesionales/123")
        String ruta,

        @Schema(description = "Momento en que ocurrió el error")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp
) {
    public static ErrorResponse of(int codigo, String mensaje, String ruta) {
        return new ErrorResponse(codigo, mensaje, ruta, LocalDateTime.now());
    }
}
