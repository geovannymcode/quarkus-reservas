package com.geovannycode.reservas.infrastructure.mapper;

import com.geovannycode.reservas.application.dto.response.HorarioDisponibleResponse;
import com.geovannycode.reservas.domain.model.HorarioDisponible;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mapper para conversión entre la entidad {@link HorarioDisponible} y sus DTOs.
 */
@ApplicationScoped
public class HorarioDisponibleMapper {

    private final ProfesionalMapper profesionalMapper;

    public HorarioDisponibleMapper(ProfesionalMapper profesionalMapper) {
        this.profesionalMapper = profesionalMapper;
    }

    /**
     * Convierte una entidad HorarioDisponible a su DTO de respuesta.
     */
    public HorarioDisponibleResponse toResponse(HorarioDisponible horario) {
        return new HorarioDisponibleResponse(
                horario.getId(),
                profesionalMapper.toResponse(horario.getProfesional()),
                horario.getFecha(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                horario.isEstado()
        );
    }
}
