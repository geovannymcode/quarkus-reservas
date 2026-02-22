package com.geovannycode.reservas.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO de entrada para crear o actualizar un Cliente.
 * Usa Java Record para inmutabilidad y concisión.
 */
@Schema(description = "Datos requeridos para registrar o actualizar un cliente")
public record ClienteRequest(

        @NotBlank(message = "El nombre del cliente es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        @Schema(description = "Nombres del cliente", example = "Ana María")
        String nombres,

        @NotBlank(message = "Los apellidos del cliente son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar 100 caracteres")
        @Schema(description = "Apellidos del cliente", example = "Torres Vega")
        String apellidos,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 255, message = "El email no puede superar 255 caracteres")
        @Schema(description = "Email único del cliente", example = "ana.torres@email.com")
        String email,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^[+]?[0-9\\s\\-]{7,20}$", message = "El teléfono no tiene un formato válido")
        @Schema(description = "Teléfono de contacto", example = "+57 310 456 7890")
        String telefono,

        @Schema(description = "Estado activo del cliente (true por defecto)", example = "true")
        Boolean estadoActivo
) {
}
