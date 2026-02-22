package com.geovannycode.reservas.infrastructure.repository;

import com.geovannycode.reservas.domain.model.Profesional;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio reactivo para la entidad {@link Profesional}.
 * Extiende {@link PanacheRepositoryBase} para obtener operaciones CRUD reactivas base.
 */
@ApplicationScoped
public class ProfesionalRepository implements PanacheRepositoryBase<Profesional, UUID> {

    /**
     * Busca todos los profesionales con estado activo.
     */
    public Uni<List<Profesional>> findAllActivos() {
        return list("estadoActivo", true);
    }

    /**
     * Verifica si existe un profesional con el ID dado.
     */
    public Uni<Boolean> existsById(UUID id) {
        return count("id", id).map(count -> count > 0);
    }
}
