package com.geovannycode.reservas.application.service;

import com.geovannycode.reservas.application.dto.request.ReservaRequest;
import com.geovannycode.reservas.application.dto.response.ProfesionalConReservasResponse;
import com.geovannycode.reservas.application.dto.response.ReservaResponse;
import com.geovannycode.reservas.domain.enums.EstadoReserva;
import com.geovannycode.reservas.domain.exception.BusinessException;
import com.geovannycode.reservas.domain.exception.EntidadInactivaException;
import com.geovannycode.reservas.domain.exception.HorarioNoDisponibleException;
import com.geovannycode.reservas.domain.exception.ReservaSolapamientoException;
import com.geovannycode.reservas.domain.exception.ResourceNotFoundException;
import com.geovannycode.reservas.domain.model.Profesional;
import com.geovannycode.reservas.domain.model.Reserva;
import com.geovannycode.reservas.infrastructure.mapper.ProfesionalMapper;
import com.geovannycode.reservas.infrastructure.mapper.ReservaMapper;
import com.geovannycode.reservas.infrastructure.repository.ClienteRepository;
import com.geovannycode.reservas.infrastructure.repository.HorarioDisponibleRepository;
import com.geovannycode.reservas.infrastructure.repository.ProfesionalRepository;
import com.geovannycode.reservas.infrastructure.repository.ReservaRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para la gestión de Reservas.
 * <p>
 * Aplica todas las reglas de negocio del dominio:
 * <ul>
 *   <li>Verificación de disponibilidad horaria antes de crear una reserva.</li>
 *   <li>Prevención de solapamientos de reservas activas.</li>
 *   <li>Validación de estado activo de cliente y profesional.</li>
 *   <li>Liberación de disponibilidad al cancelar.</li>
 * </ul>
 * <p>
 * El método {@link #listarProfesionalesPorReservasActivas()} está protegido
 * con SmallRye Fault Tolerance (@Retry, @Timeout, @Fallback).
 */
@ApplicationScoped
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ProfesionalRepository profesionalRepository;
    private final ClienteRepository clienteRepository;
    private final HorarioDisponibleRepository horarioRepository;
    private final ReservaMapper reservaMapper;
    private final ProfesionalMapper profesionalMapper;

    public ReservaService(ReservaRepository reservaRepository,
                          ProfesionalRepository profesionalRepository,
                          ClienteRepository clienteRepository,
                          HorarioDisponibleRepository horarioRepository,
                          ReservaMapper reservaMapper,
                          ProfesionalMapper profesionalMapper) {
        this.reservaRepository = reservaRepository;
        this.profesionalRepository = profesionalRepository;
        this.clienteRepository = clienteRepository;
        this.horarioRepository = horarioRepository;
        this.reservaMapper = reservaMapper;
        this.profesionalMapper = profesionalMapper;
    }

    @WithSession
    public Uni<List<ReservaResponse>> listarTodas() {
        return reservaRepository.listAll()
                .map(reservas -> reservas.stream()
                        .map(reservaMapper::toResponse)
                        .collect(Collectors.toList()));
    }

    @WithSession
    public Uni<ReservaResponse> buscarPorId(UUID id) {
        return reservaRepository.findById(id)
                .onItem().ifNull().failWith(() ->
                        new ResourceNotFoundException(
                                String.format("Reserva no encontrada con id: %s", id)))
                .map(reservaMapper::toResponse);
    }

    /**
     * Crea una reserva aplicando todas las reglas de negocio:
     * 1. Cliente y profesional deben existir y estar activos.
     * 2. Debe existir un HorarioDisponible que cubra el intervalo solicitado.
     * 3. No deben existir reservas activas solapadas para el mismo profesional.
     */
    @WithTransaction
    public Uni<ReservaResponse> crearReserva(ReservaRequest request) {
        if (!request.horaFin().isAfter(request.horaInicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }

        return profesionalRepository.findById(request.profesionalId())
                .onItem().ifNull().failWith(() ->
                        new ResourceNotFoundException(
                                String.format("Profesional no encontrado con id: %s", request.profesionalId())))
                .flatMap(profesional -> {
                    if (!profesional.isEstadoActivo()) {
                        throw new EntidadInactivaException(
                                String.format("El profesional '%s %s' no está activo",
                                        profesional.getNombres(), profesional.getApellidos()));
                    }
                    return clienteRepository.findById(request.clienteId())
                            .onItem().ifNull().failWith(() ->
                                    new ResourceNotFoundException(
                                            String.format("Cliente no encontrado con id: %s", request.clienteId())))
                            .flatMap(cliente -> {
                                if (!cliente.isEstadoActivo()) {
                                    throw new EntidadInactivaException(
                                            String.format("El cliente '%s %s' no está activo",
                                                    cliente.getNombres(), cliente.getApellidos()));
                                }
                                return horarioRepository.findDisponibleParaReserva(
                                                request.profesionalId(),
                                                request.fecha(),
                                                request.horaInicio(),
                                                request.horaFin())
                                        .flatMap(horarioOpt -> {
                                            var horario = horarioOpt.orElseThrow(() ->
                                                    new HorarioNoDisponibleException(
                                                            String.format(
                                                                    "No existe horario disponible para el profesional en la fecha %s de %s a %s",
                                                                    request.fecha(), request.horaInicio(), request.horaFin())));

                                            return reservaRepository.findSolapadasActivas(
                                                            request.profesionalId(),
                                                            request.fecha(),
                                                            request.horaInicio(),
                                                            request.horaFin())
                                                    .flatMap(solapadas -> {
                                                        if (!solapadas.isEmpty()) {
                                                            throw new ReservaSolapamientoException(
                                                                    String.format(
                                                                            "El profesional ya tiene una reserva activa que se solapa en la fecha %s entre %s y %s",
                                                                            request.fecha(), request.horaInicio(), request.horaFin()));
                                                        }
                                                        horario.setEstado(false);

                                                        var nuevaReserva = Reserva.builder()
                                                                .fecha(request.fecha())
                                                                .horaInicio(request.horaInicio())
                                                                .horaFin(request.horaFin())
                                                                .cliente(cliente)
                                                                .profesional(profesional)
                                                                .estado(EstadoReserva.CREADA)
                                                                .build();

                                                        return reservaRepository.persist(nuevaReserva);
                                                    });
                                        });
                            });
                })
                .map(reservaMapper::toResponse);
    }

    /**
     * Cancela una reserva cambiando su estado a CANCELADA y liberando el horario asociado.
     */
    @WithTransaction
    public Uni<ReservaResponse> cancelarReserva(UUID id) {
        return reservaRepository.findById(id)
                .onItem().ifNull().failWith(() ->
                        new ResourceNotFoundException(
                                String.format("Reserva no encontrada con id: %s", id)))
                .flatMap(reserva -> {
                    if (reserva.getEstado() == EstadoReserva.CANCELADA) {
                        throw new BusinessException("La reserva ya se encuentra en estado CANCELADA",
                                jakarta.ws.rs.core.Response.Status.BAD_REQUEST);
                    }
                    if (reserva.getEstado() == EstadoReserva.COMPLETADA) {
                        throw new BusinessException("No se puede cancelar una reserva ya COMPLETADA",
                                jakarta.ws.rs.core.Response.Status.BAD_REQUEST);
                    }

                    reserva.setEstado(EstadoReserva.CANCELADA);

                    // Liberar el horario que fue bloqueado por esta reserva
                    return horarioRepository.findOcupadoPorReserva(
                                    reserva.getProfesional().getId(),
                                    reserva.getFecha(),
                                    reserva.getHoraInicio(),
                                    reserva.getHoraFin())
                            .map(horarioOpt -> {
                                horarioOpt.ifPresent(h -> h.setEstado(true));
                                return reservaMapper.toResponse(reserva);
                            });
                });
    }

    /**
     * Lista los profesionales ordenados de forma descendente por número de reservas activas.
     * El conteo y ordenamiento se realiza en memoria usando programación funcional (Streams).
     * <p>
     * Protegido con SmallRye Fault Tolerance: reintenta hasta 3 veces con 200ms de espera,
     * timeout de 10s y fallback a lista vacía si falla.
     */
    @WithSession
    @Retry(maxRetries = 3, delay = 200, delayUnit = ChronoUnit.MILLIS)
    @Timeout(value = 10, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "listarProfesionalesVacio")
    public Uni<List<ProfesionalConReservasResponse>> listarProfesionalesPorReservasActivas() {
        return reservaRepository.findAllActivas()
                .map(reservas -> reservas.stream()
                        // Agrupar por profesional y contar reservas activas
                        .collect(Collectors.groupingBy(
                                Reserva::getProfesional,
                                Collectors.counting()))
                        .entrySet().stream()
                        // Ordenar descendente por total de reservas activas
                        .sorted(Map.Entry.<Profesional, Long>comparingByValue().reversed())
                        .map(entry -> new ProfesionalConReservasResponse(
                                profesionalMapper.toResponse(entry.getKey()),
                                entry.getValue()))
                        .collect(Collectors.toList()));
    }

    /**
     * Fallback para {@link #listarProfesionalesPorReservasActivas()}.
     * Retorna una lista vacía en caso de fallo del servicio.
     */
    public Uni<List<ProfesionalConReservasResponse>> listarProfesionalesVacio() {
        return Uni.createFrom().item(Collections.emptyList());
    }

    /**
     * Muestra la relación de fechas con sus reservas correspondientes.
     * Procesado en memoria usando programación funcional con un Map&lt;LocalDate, List&lt;ReservaResponse&gt;&gt;.
     * <p>
     * Ejemplo de salida:
     * <pre>
     * 2025-11-10: [Reserva 1 (Ana Torres / Luis Salazar), Reserva 2 ...]
     * 2025-11-12: [Reserva 3 (Mito X / Code Y)]
     * </pre>
     */
    @WithSession
    public Uni<Map<LocalDate, List<ReservaResponse>>> listarReservasPorFecha() {
        return reservaRepository.findAllActivas()
                .map(reservas -> reservas.stream()
                        .map(reservaMapper::toResponse)
                        .collect(Collectors.groupingBy(
                                ReservaResponse::fecha,
                                TreeMap::new,  // TreeMap para ordenar fechas cronológicamente
                                Collectors.toList())));
    }
}
