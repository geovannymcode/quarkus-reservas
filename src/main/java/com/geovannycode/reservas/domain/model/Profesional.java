package com.geovannycode.reservas.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidad que representa a un profesional del centro de servicios.
 * <p>
 * Ejemplo de profesionales: psicólogos, mentores, asesores, tutores.
 */
@Entity
@Table(name = "profesionales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profesional {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "especialidad", nullable = false, length = 150)
    private String especialidad;

    @Column(name = "estado_activo", nullable = false)
    @Builder.Default
    private boolean estadoActivo = true;
}

