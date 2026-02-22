package com.geovannycode.reservas.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidad que representa a un cliente que solicita servicios profesionales.
 */
@Entity
@Table(name = "clientes",
        uniqueConstraints = @UniqueConstraint(name = "uk_clientes_email", columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Column(name = "estado_activo", nullable = false)
    @Builder.Default
    private boolean estadoActivo = true;
}
