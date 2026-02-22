package com.geovannycode.reservas.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO de entrada para crear o actualizar un Profesional.
 * Usa Java Record para inmutabilidad y concisión (Java 16+).
 */
@Schema(description = "Datos requeridos para registrar o actualizar un profesional")
public record ProfesionalRequest(

        @NotBlank(message = "El nombre del profesional es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        @Schema(description = "Nombres del profesional", example = "Luis Alberto")
        String nombres,

        @NotBlank(message = "Los apellidos del profesional son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar 100 caracteres")
        @Schema(description = "Apellidos del profesional", example = "Salazar Ríos")
        String apellidos,

        @NotBlank(message = "La especialidad es obligatoria")
        @Size(max = 150, message = "La especialidad no puede superar 150 caracteres")
        @Schema(description = "Especialidad del profesional", example = "Psicología Clínica")
        String especialidad,

        @Schema(description = "Estado activo del profesional (true por defecto)", example = "true")
        Boolean estadoActivo
) {
}
