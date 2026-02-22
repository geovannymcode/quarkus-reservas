package com.geovannycode.reservas.infrastructure.mapper;

import com.geovannycode.reservas.application.dto.request.ProfesionalRequest;
import com.geovannycode.reservas.application.dto.response.ProfesionalResponse;
import com.geovannycode.reservas.domain.model.Profesional;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mapper para conversión entre la entidad {@link Profesional} y sus DTOs.
 * Centraliza la transformación de datos para mantener el SRP en servicios y recursos.
 */
@ApplicationScoped
public class ProfesionalMapper {

    /**
     * Convierte una entidad Profesional a su DTO de respuesta.
     */
    public ProfesionalResponse toResponse(Profesional profesional) {
        return new ProfesionalResponse(
                profesional.getId(),
                profesional.getNombres(),
                profesional.getApellidos(),
                profesional.getEspecialidad(),
                profesional.isEstadoActivo()
        );
    }

    /**
     * Convierte un DTO de request a una entidad Profesional nueva (sin ID).
     */
    public Profesional toEntity(ProfesionalRequest request) {
        return Profesional.builder()
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .especialidad(request.especialidad())
                .estadoActivo(request.estadoActivo() != null ? request.estadoActivo() : true)
                .build();
    }
}

