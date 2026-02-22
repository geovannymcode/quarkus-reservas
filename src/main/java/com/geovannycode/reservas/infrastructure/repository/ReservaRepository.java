package com.geovannycode.reservas.infrastructure.repository;

import com.geovannycode.reservas.domain.enums.EstadoReserva;
import com.geovannycode.reservas.domain.model.Reserva;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio reactivo para la entidad {@link Reserva}.
 * Contiene las consultas especializadas para validar solapamientos y consultas funcionales.
 */
@ApplicationScoped
public class ReservaRepository implements PanacheRepositoryBase<Reserva, UUID> {

    /**
     * Busca todas las reservas en estado CREADA (activas).
     * Usado para el ranking funcional de profesionales y la vista por fecha.
     */
    public Uni<List<Reserva>> findAllActivas() {
        return list("estado", EstadoReserva.CREADA);
    }

    /**
     * Busca reservas activas de un profesional que se solapen con el intervalo dado.
     * <p>
     * Lógica de solapamiento: existe solapamiento si
     * {@code existente.horaInicio < horaFin && existente.horaFin > horaInicio}.
     *
     * @param profesionalId ID del profesional
     * @param fecha         Fecha a verificar
     * @param horaInicio    Hora de inicio de la nueva reserva
     * @param horaFin       Hora de fin de la nueva reserva
     */
    public Uni<List<Reserva>> findSolapadasActivas(UUID profesionalId,
                                                   LocalDate fecha,
                                                   LocalTime horaInicio,
                                                   LocalTime horaFin) {
        return list(
                "profesional.id = ?1 AND fecha = ?2 AND estado = ?3 " +
                        "AND horaInicio < ?4 AND horaFin > ?5",
                profesionalId, fecha, EstadoReserva.CREADA, horaFin, horaInicio);
    }

    /**
     * Busca todas las reservas de un cliente específico.
     */
    public Uni<List<Reserva>> findByClienteId(UUID clienteId) {
        return list("cliente.id", clienteId);
    }

    /**
     * Busca todas las reservas de un profesional específico.
     */
    public Uni<List<Reserva>> findByProfesionalId(UUID profesionalId) {
        return list("profesional.id", profesionalId);
    }

    /**
     * Busca todas las reservas de una fecha específica.
     */
    public Uni<List<Reserva>> findByFecha(LocalDate fecha) {
        return list("fecha", fecha);
    }
}

