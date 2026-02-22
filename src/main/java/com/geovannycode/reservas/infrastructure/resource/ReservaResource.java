package com.geovannycode.reservas.infrastructure.resource;

import com.geovannycode.reservas.application.dto.request.ReservaRequest;
import com.geovannycode.reservas.application.dto.response.ReservaResponse;
import com.geovannycode.reservas.application.service.ReservaService;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Recurso REST para la gestión de Reservas.
 * <p>
 * Incluye endpoints de consulta funcional:
 * <ul>
 *   <li>Reservas agrupadas por fecha (usando {@code Map<LocalDate, List<ReservaResponse>>}).</li>
 * </ul>
 */
@Path("/api/reservas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Reservas", description = "Gestión de reservas entre clientes y profesionales")
public class ReservaResource {

    private final ReservaService reservaService;

    public ReservaResource(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GET
    @Operation(summary = "Listar todas las reservas")
    @APIResponse(responseCode = "200", description = "Lista de reservas")
    public Uni<List<ReservaResponse>> listarTodas() {
        return reservaService.listarTodas();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar reserva por ID")
    @APIResponse(responseCode = "200", description = "Reserva encontrada")
    @APIResponse(responseCode = "404", description = "Reserva no encontrada")
    public Uni<Response> buscarPorId(@PathParam("id") UUID id) {
        return reservaService.buscarPorId(id)
                .map(reserva -> Response.ok(reserva).build());
    }

    @POST
    @Operation(summary = "Crear una nueva reserva",
            description = "Crea una reserva aplicando todas las reglas de negocio: " +
                    "verifica disponibilidad horaria, previene solapamientos y valida estados activos.")
    @APIResponse(responseCode = "201", description = "Reserva creada exitosamente")
    @APIResponse(responseCode = "400", description = "Datos inválidos o no hay horario disponible")
    @APIResponse(responseCode = "404", description = "Cliente o profesional no encontrado")
    @APIResponse(responseCode = "409", description = "Solapamiento con otra reserva activa")
    @APIResponse(responseCode = "422", description = "Cliente o profesional inactivo")
    public Uni<Response> crear(@Valid ReservaRequest request) {
        return reservaService.crearReserva(request)
                .map(reserva -> Response
                        .created(URI.create("/api/reservas/" + reserva.id()))
                        .entity(reserva)
                        .build());
    }

    @PATCH
    @Path("/{id}/cancelar")
    @Operation(summary = "Cancelar una reserva",
            description = "Cambia el estado de la reserva a CANCELADA y libera el horario ocupado.")
    @APIResponse(responseCode = "200", description = "Reserva cancelada exitosamente")
    @APIResponse(responseCode = "400", description = "La reserva ya está cancelada o completada")
    @APIResponse(responseCode = "404", description = "Reserva no encontrada")
    public Uni<Response> cancelar(@PathParam("id") UUID id) {
        return reservaService.cancelarReserva(id)
                .map(reserva -> Response.ok(reserva).build());
    }

    @GET
    @Path("/agrupadas/por-fecha")
    @Operation(summary = "Reservas agrupadas por fecha",
            description = "Retorna un mapa de fechas con sus reservas activas correspondientes. " +
                    "Procesado en memoria con programación funcional (Map<LocalDate, List<Reserva>>). " +
                    "Ejemplo: 2025-11-10: [Reserva 1 (Ana Torres / Luis Salazar), ...]")
    @APIResponse(responseCode = "200", description = "Mapa de fechas con reservas activas")
    public Uni<Map<LocalDate, List<ReservaResponse>>> reservasPorFecha() {
        return reservaService.listarReservasPorFecha();
    }
}
