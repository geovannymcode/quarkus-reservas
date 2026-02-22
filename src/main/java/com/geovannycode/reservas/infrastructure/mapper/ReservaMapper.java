package com.geovannycode.reservas.infrastructure.mapper;

import com.geovannycode.reservas.application.dto.response.ReservaResponse;
import com.geovannycode.reservas.domain.model.Reserva;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mapper para conversión entre la entidad {@link Reserva} y sus DTOs.
 */
@ApplicationScoped
public class ReservaMapper {

    private final ClienteMapper clienteMapper;
    private final ProfesionalMapper profesionalMapper;

    public ReservaMapper(ClienteMapper clienteMapper, ProfesionalMapper profesionalMapper) {
        this.clienteMapper = clienteMapper;
        this.profesionalMapper = profesionalMapper;
    }

    /**
     * Convierte una entidad Reserva a su DTO de respuesta con objetos anidados.
     */
    public ReservaResponse toResponse(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                clienteMapper.toResponse(reserva.getCliente()),
                profesionalMapper.toResponse(reserva.getProfesional()),
                reserva.getEstado()
        );
    }
}
