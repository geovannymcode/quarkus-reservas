package com.geovannycode.reservas.application.service;

import com.geovannycode.reservas.application.dto.request.ProfesionalRequest;
import com.geovannycode.reservas.application.dto.response.ProfesionalResponse;
import com.geovannycode.reservas.domain.exception.ResourceNotFoundException;
import com.geovannycode.reservas.infrastructure.mapper.ProfesionalMapper;
import com.geovannycode.reservas.infrastructure.repository.ProfesionalRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para la gestión de Profesionales.
 * Orquesta las operaciones CRUD delegando persistencia al repositorio
 * y transformación de datos al mapper.
 */
@ApplicationScoped
public class ProfesionalService {

    private final ProfesionalRepository profesionalRepository;
    private final ProfesionalMapper profesionalMapper;

    public ProfesionalService(ProfesionalRepository profesionalRepository,
                              ProfesionalMapper profesionalMapper) {
        this.profesionalRepository = profesionalRepository;
        this.profesionalMapper = profesionalMapper;
    }

    @WithSession
    public Uni<List<ProfesionalResponse>> listarTodos() {
        return profesionalRepository.listAll()
                .map(profesionales -> profesionales.stream()
                        .map(profesionalMapper::toResponse)
                        .collect(Collectors.toList()));
    }

    @WithSession
    public Uni<ProfesionalResponse> buscarPorId(UUID id) {
        return profesionalRepository.findById(id)
                .onItem().ifNull().failWith(() ->
                        new ResourceNotFoundException(
                                String.format("Profesional no encontrado con id: %s", id)))
                .map(profesionalMapper::toResponse);
    }

    @WithTransaction
    public Uni<ProfesionalResponse> crear(ProfesionalRequest request) {
        var profesional = profesionalMapper.toEntity(request);
        return profesionalRepository.persist(profesional)
                .map(profesionalMapper::toResponse);
    }

    @WithTransaction
    public Uni<ProfesionalResponse> actualizar(UUID id, ProfesionalRequest request) {
        return profesionalRepository.findById(id)
                .onItem().ifNull().failWith(() ->
                        new ResourceNotFoundException(
                                String.format("Profesional no encontrado con id: %s", id)))
                .map(profesional -> {
                    profesional.setNombres(request.nombres());
                    profesional.setApellidos(request.apellidos());
                    profesional.setEspecialidad(request.especialidad());
                    if (request.estadoActivo() != null) {
                        profesional.setEstadoActivo(request.estadoActivo());
                    }
                    return profesionalMapper.toResponse(profesional);
                });
    }

    @WithTransaction
    public Uni<Void> eliminar(UUID id) {
        return profesionalRepository.findById(id)
                .onItem().ifNull().failWith(() ->
                        new ResourceNotFoundException(
                                String.format("Profesional no encontrado con id: %s", id)))
                .flatMap(profesional -> profesionalRepository.delete(profesional));
    }
}
