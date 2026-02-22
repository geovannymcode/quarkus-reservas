package com.geovannycode.reservas.application.service;

import com.geovannycode.reservas.application.dto.request.ClienteRequest;
import com.geovannycode.reservas.application.dto.response.ClienteResponse;
import com.geovannycode.reservas.domain.exception.EmailDuplicadoException;
import com.geovannycode.reservas.domain.exception.ResourceNotFoundException;
import com.geovannycode.reservas.infrastructure.mapper.ClienteMapper;
import com.geovannycode.reservas.infrastructure.repository.ClienteRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para la gestión de Clientes.
 * Garantiza la unicidad del email y orquesta las operaciones CRUD.
 */
@ApplicationScoped
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @WithSession
    public Uni<List<ClienteResponse>> listarTodos() {
        return clienteRepository.listAll()
                .map(clientes -> clientes.stream()
                        .map(clienteMapper::toResponse)
                        .collect(Collectors.toList()));
    }

    @WithSession
    public Uni<ClienteResponse> buscarPorId(UUID id) {
        return clienteRepository.findById(id)
                .onItem().ifNull().failWith(() ->
                        new ResourceNotFoundException(
                                String.format("Cliente no encontrado con id: %s", id)))
                .map(clienteMapper::toResponse);
    }

    @WithTransaction
    public Uni<ClienteResponse> crear(ClienteRequest request) {
        return clienteRepository.existsByEmail(request.email())
                .flatMap(existe -> {
                    if (existe) {
                        throw new EmailDuplicadoException(request.email());
                    }
                    var cliente = clienteMapper.toEntity(request);
                    return clienteRepository.persist(cliente);
                })
                .map(clienteMapper::toResponse);
    }

    @WithTransaction
    public Uni<ClienteResponse> actualizar(UUID id, ClienteRequest request) {
        return clienteRepository.findById(id)
                .onItem().ifNull().failWith(() ->
                        new ResourceNotFoundException(
                                String.format("Cliente no encontrado con id: %s", id)))
                .flatMap(cliente -> {
                    // Verificar email duplicado solo si cambió
                    if (!cliente.getEmail().equalsIgnoreCase(request.email())) {
                        return clienteRepository.existsByEmail(request.email())
                                .map(existe -> {
                                    if (existe) {
                                        throw new EmailDuplicadoException(request.email());
                                    }
                                    return cliente;
                                });
                    }
                    return Uni.createFrom().item(cliente);
                })
                .map(cliente -> {
                    cliente.setNombres(request.nombres());
                    cliente.setApellidos(request.apellidos());
                    cliente.setEmail(request.email());
                    cliente.setTelefono(request.telefono());
                    if (request.estadoActivo() != null) {
                        cliente.setEstadoActivo(request.estadoActivo());
                    }
                    return clienteMapper.toResponse(cliente);
                });
    }

    @WithTransaction
    public Uni<Void> eliminar(UUID id) {
        return clienteRepository.findById(id)
                .onItem().ifNull().failWith(() ->
                        new ResourceNotFoundException(
                                String.format("Cliente no encontrado con id: %s", id)))
                .flatMap(cliente -> clienteRepository.delete(cliente));
    }
}

