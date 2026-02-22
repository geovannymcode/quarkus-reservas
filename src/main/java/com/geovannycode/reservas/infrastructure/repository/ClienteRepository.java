package com.geovannycode.reservas.infrastructure.repository;

import com.geovannycode.reservas.domain.model.Cliente;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio reactivo para la entidad {@link Cliente}.
 * Extiende {@link PanacheRepositoryBase} para obtener operaciones CRUD reactivas base.
 */
@ApplicationScoped
public class ClienteRepository implements PanacheRepositoryBase<Cliente, UUID> {

    /**
     * Verifica si ya existe un cliente con el email dado.
     */
    public Uni<Boolean> existsByEmail(String email) {
        return count("lower(email)", email.toLowerCase()).map(count -> count > 0);
    }

    /**
     * Busca un cliente por su email (único en el sistema).
     */
    public Uni<Optional<Cliente>> findByEmail(String email) {
        return find("lower(email)", email.toLowerCase())
                .firstResult()
                .map(Optional::ofNullable);
    }

    /**
     * Busca todos los clientes activos.
     */
    public Uni<List<Cliente>> findAllActivos() {
        return list("estadoActivo", true);
    }
}

