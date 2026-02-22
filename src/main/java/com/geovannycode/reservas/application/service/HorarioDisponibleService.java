package com.geovannycode.reservas.application.service;

import com.geovannycode.reservas.application.dto.request.HorarioDisponibleRequest;
import com.geovannycode.reservas.application.dto.response.HorarioDisponibleResponse;
import com.geovannycode.reservas.domain.exception.HorarioSolapamientoException;
import com.geovannycode.reservas.domain.exception.ResourceNotFoundException;
import com.geovannycode.reservas.domain.model.HorarioDisponible;
import com.geovannycode.reservas.infrastructure.mapper.HorarioDisponibleMapper;
import com.geovannycode.reservas.infrastructure.repository.HorarioDisponibleRepository;
import com.geovannycode.reservas.infrastructure.repository.ProfesionalRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para registrar y consultar los horarios disponibles de profesionales.
 * <p>
 * Regla clave: no se permiten horarios solapados para el mismo profesional en la misma fecha.
 */
@ApplicationScoped
public class HorarioDisponibleService {

    private final HorarioDisponibleRepository horarioRepository;
    private final ProfesionalRepository profesionalRepository;
    private final HorarioDisponibleMapper horarioMapper;

    public HorarioDisponibleService(HorarioDisponibleRepository horarioRepository,
                                    ProfesionalRepository profesionalRepository,
                                    HorarioDisponibleMapper horarioMapper) {
        this.horarioRepository = horarioRepository;
        this.profesionalRepository = profesionalRepository;
        this.horarioMapper = horarioMapper;
    }

    @WithSession
    public Uni<List<HorarioDisponibleResponse>> listarTodos() {
        return horarioRepository.listAll()
                .map(horarios -> horarios.stream()
                        .map(horarioMapper::toResponse)
                        .collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<HorarioDisponibleResponse>> listarPorProfesional(UUID profesionalId) {
        return horarioRepository.findByProfesionalId(profesionalId)
                .map(horarios -> horarios.stream()
                        .map(horarioMapper::toResponse)
                        .collect(Collectors.toList()));
    }

    @WithTransaction
    public Uni<HorarioDisponibleResponse> registrar(HorarioDisponibleRequest request) {
        // Validar que el horario fin sea posterior al inicio
        if (!request.horaFin().isAfter(request.horaInicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }

        return profesionalRepository.findById(request.profesionalId())
                .onItem().ifNull().failWith(() ->
                        new ResourceNotFoundException(
                                String.format("Profesional no encontrado con id: %s", request.profesionalId())))
                .flatMap(profesional ->
                        horarioRepository.findSolapados(
                                        request.profesionalId(),
                                        request.fecha(),
                                        request.horaInicio(),
                                        request.horaFin())
                                .flatMap(solapados -> {
                                    if (!solapados.isEmpty()) {
                                        throw new HorarioSolapamientoException(
                                                String.format(
                                                        "El profesional ya tiene un horario disponible que se solapa " +
                                                                "en la fecha %s entre %s y %s",
                                                        request.fecha(), request.horaInicio(), request.horaFin()));
                                    }
                                    var horario = HorarioDisponible.builder()
                                            .profesional(profesional)
                                            .fecha(request.fecha())
                                            .horaInicio(request.horaInicio())
                                            .horaFin(request.horaFin())
                                            .estado(true)
                                            .build();
                                    return horarioRepository.persist(horario);
                                }))
                .map(horarioMapper::toResponse);
    }

    @WithTransaction
    public Uni<Void> eliminar(UUID id) {
        return horarioRepository.findById(id)
                .onItem().ifNull().failWith(() ->
                        new ResourceNotFoundException(
                                String.format("Horario disponible no encontrado con id: %s", id)))
                .flatMap(horario -> horarioRepository.delete(horario));
    }
}

