package com.geovannycode.reservas.infrastructure.repository;

import com.geovannycode.reservas.domain.model.HorarioDisponible;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio reactivo para la entidad {@link HorarioDisponible}.
 * Contiene las consultas especializadas para validar solapamientos y disponibilidad.
 */
@ApplicationScoped
public class HorarioDisponibleRepository implements PanacheRepositoryBase<HorarioDisponible, UUID> {

    /**
     * Busca horarios disponibles de un profesional que se solapen con el intervalo dado.
     * <p>
     * Lógica de solapamiento: existe solapamiento si
     * {@code existente.horaInicio < horaFin && existente.horaFin > horaInicio}.
     *
     * @param profesionalId ID del profesional
     * @param fecha         Fecha a verificar
     * @param horaInicio    Hora de inicio del nuevo bloque
     * @param horaFin       Hora de fin del nuevo bloque
     */
    public Uni<List<HorarioDisponible>> findSolapados(UUID profesionalId,
                                                      LocalDate fecha,
                                                      LocalTime horaInicio,
                                                      LocalTime horaFin) {
        return list(
                "profesional.id = ?1 AND fecha = ?2 AND horaInicio < ?3 AND horaFin > ?4",
                profesionalId, fecha, horaFin, horaInicio);
    }

    /**
     * Busca el horario disponible (estado=true) de un profesional que cubra completamente
     * el intervalo solicitado para una reserva.
     *
     * @param profesionalId ID del profesional
     * @param fecha         Fecha de la reserva
     * @param horaInicio    Hora de inicio de la reserva
     * @param horaFin       Hora de fin de la reserva
     */
    public Uni<Optional<HorarioDisponible>> findDisponibleParaReserva(UUID profesionalId,
                                                                      LocalDate fecha,
                                                                      LocalTime horaInicio,
                                                                      LocalTime horaFin) {
        return find(
                "profesional.id = ?1 AND fecha = ?2 AND estado = true " +
                        "AND horaInicio <= ?3 AND horaFin >= ?4",
                profesionalId, fecha, horaInicio, horaFin)
                .firstResult()
                .map(Optional::ofNullable);
    }

    /**
     * Busca el horario ocupado (estado=false) de un profesional que cubra el intervalo dado.
     * Usado al cancelar una reserva para liberar la disponibilidad.
     *
     * @param profesionalId ID del profesional
     * @param fecha         Fecha de la reserva
     * @param horaInicio    Hora de inicio de la reserva
     * @param horaFin       Hora de fin de la reserva
     */
    public Uni<Optional<HorarioDisponible>> findOcupadoPorReserva(UUID profesionalId,
                                                                  LocalDate fecha,
                                                                  LocalTime horaInicio,
                                                                  LocalTime horaFin) {
        return find(
                "profesional.id = ?1 AND fecha = ?2 AND estado = false " +
                        "AND horaInicio <= ?3 AND horaFin >= ?4",
                profesionalId, fecha, horaInicio, horaFin)
                .firstResult()
                .map(Optional::ofNullable);
    }

    /**
     * Busca todos los horarios de un profesional específico.
     */
    public Uni<List<HorarioDisponible>> findByProfesionalId(UUID profesionalId) {
        return list("profesional.id", profesionalId);
    }
}
