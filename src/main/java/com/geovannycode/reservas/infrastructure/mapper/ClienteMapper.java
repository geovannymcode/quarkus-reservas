package com.geovannycode.reservas.infrastructure.mapper;

import com.geovannycode.reservas.application.dto.request.ClienteRequest;
import com.geovannycode.reservas.application.dto.response.ClienteResponse;
import com.geovannycode.reservas.domain.model.Cliente;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mapper para conversión entre la entidad {@link Cliente} y sus DTOs.
 * Centraliza la transformación de datos para mantener el SRP en servicios y recursos.
 */
@ApplicationScoped
public class ClienteMapper {

    /**
     * Convierte una entidad Cliente a su DTO de respuesta.
     */
    public ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.isEstadoActivo()
        );
    }

    /**
     * Convierte un DTO de request a una entidad Cliente nueva (sin ID).
     */
    public Cliente toEntity(ClienteRequest request) {
        return Cliente.builder()
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .email(request.email())
                .telefono(request.telefono())
                .estadoActivo(request.estadoActivo() != null ? request.estadoActivo() : true)
                .build();
    }
}
