package com.geovannycode.reservas.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Entidad que representa un bloque de tiempo en que un profesional
 * está disponible para atender clientes.
 * <p>
 * Regla de negocio: no puede haber solapamiento de horarios para
 * el mismo profesional en la misma fecha.
 */
@Entity
@Table(name = "horarios_disponibles",
        indexes = @Index(name = "idx_horarios_profesional_fecha", columnList = "profesional_id, fecha"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDisponible {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profesional_id", nullable = false)
    private Profesional profesional;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    /**
     * true = disponible para reservar, false = ocupado/bloqueado.
     */
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private boolean estado = true;
}
