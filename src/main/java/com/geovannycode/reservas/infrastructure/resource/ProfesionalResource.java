package com.geovannycode.reservas.infrastructure.resource;

import com.geovannycode.reservas.application.dto.request.ProfesionalRequest;
import com.geovannycode.reservas.application.dto.response.ProfesionalConReservasResponse;
import com.geovannycode.reservas.application.dto.response.ProfesionalResponse;
import com.geovannycode.reservas.application.service.ProfesionalService;
import com.geovannycode.reservas.application.service.ReservaService;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Recurso REST para la gestión de Profesionales.
 * Expone el CRUD completo y endpoints de consulta funcional.
 */
@Path("/api/profesionales")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Profesionales", description = "Gestión de profesionales del centro de servicios")
public class ProfesionalResource {

    private final ProfesionalService profesionalService;
    private final ReservaService reservaService;

    public ProfesionalResource(ProfesionalService profesionalService, ReservaService reservaService) {
        this.profesionalService = profesionalService;
        this.reservaService = reservaService;
    }

    @GET
    @Operation(summary = "Listar todos los profesionales",
            description = "Retorna la lista completa de profesionales registrados en el sistema")
    @APIResponse(responseCode = "200", description = "Lista de profesionales")
    public Uni<List<ProfesionalResponse>> listarTodos() {
        return profesionalService.listarTodos();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar profesional por ID")
    @APIResponse(responseCode = "200", description = "Profesional encontrado")
    @APIResponse(responseCode = "404", description = "Profesional no encontrado")
    public Uni<Response> buscarPorId(@PathParam("id") UUID id) {
        return profesionalService.buscarPorId(id)
                .map(profesional -> Response.ok(profesional).build());
    }

    @POST
    @Operation(summary = "Crear un nuevo profesional")
    @APIResponse(responseCode = "201", description = "Profesional creado exitosamente")
    @APIResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public Uni<Response> crear(@Valid ProfesionalRequest request) {
        return profesionalService.crear(request)
                .map(profesional -> Response
                        .created(URI.create("/api/profesionales/" + profesional.id()))
                        .entity(profesional)
                        .build());
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar datos de un profesional")
    @APIResponse(responseCode = "200", description = "Profesional actualizado exitosamente")
    @APIResponse(responseCode = "404", description = "Profesional no encontrado")
    public Uni<Response> actualizar(@PathParam("id") UUID id, @Valid ProfesionalRequest request) {
        return profesionalService.actualizar(id, request)
                .map(profesional -> Response.ok(profesional).build());
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar un profesional")
    @APIResponse(responseCode = "204", description = "Profesional eliminado exitosamente")
    @APIResponse(responseCode = "404", description = "Profesional no encontrado")
    public Uni<Response> eliminar(@PathParam("id") UUID id) {
        return profesionalService.eliminar(id)
                .map(v -> Response.noContent().build());
    }

    // --- Endpoints de consulta funcional ---

    @GET
    @Path("/ranking/por-reservas-activas")
    @Operation(summary = "Ranking de profesionales por reservas activas",
            description = "Lista profesionales ordenados de forma descendente por número de reservas activas. " +
                    "El conteo y ordenamiento se realiza en memoria con programación funcional (Streams).")
    @APIResponse(responseCode = "200", description = "Ranking de profesionales")
    public Uni<List<ProfesionalConReservasResponse>> rankingPorReservasActivas() {
        return reservaService.listarProfesionalesPorReservasActivas();
    }
}