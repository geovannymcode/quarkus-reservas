package com.geovannycode.reservas.domain.model;

import com.geovannycode.reservas.domain.enums.EstadoReserva;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Entidad que representa una reserva confirmada entre un cliente
 * y un profesional en un horario específico.
 * <p>
 * Reglas de negocio:
 * <ul>
 *   <li>Solo se crea si existe un {@link HorarioDisponible} que cubra el intervalo.</li>
 *   <li>No puede haber solapamiento con otras reservas activas del mismo profesional.</li>
 *   <li>Cliente y profesional deben estar activos al momento de crear la reserva.</li>
 * </ul>
 */
@Entity
@Table(name = "reservas",
        indexes = {
                @Index(name = "idx_reservas_profesional_fecha", columnList = "profesional_id, fecha"),
                @Index(name = "idx_reservas_cliente", columnList = "cliente_id"),
                @Index(name = "idx_reservas_estado", columnList = "estado")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profesional_id", nullable = false)
    private Profesional profesional;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoReserva estado = EstadoReserva.CREADA;
}

