package com.geovannycode.reservas.infrastructure.resource;

import com.geovannycode.reservas.application.dto.request.HorarioDisponibleRequest;
import com.geovannycode.reservas.application.dto.response.HorarioDisponibleResponse;
import com.geovannycode.reservas.application.service.HorarioDisponibleService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Recurso REST para la gestión de Horarios Disponibles de profesionales.
 * Valida la no superposición de horarios antes de registrar.
 */
@Path("/api/horarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Horarios Disponibles", description = "Registro y consulta de disponibilidad horaria de profesionales")
public class HorarioDisponibleResource {

    private final HorarioDisponibleService horarioService;

    public HorarioDisponibleResource(HorarioDisponibleService horarioService) {
        this.horarioService = horarioService;
    }

    @GET
    @Operation(summary = "Listar todos los horarios disponibles")
    @APIResponse(responseCode = "200", description = "Lista de horarios")
    public Uni<List<HorarioDisponibleResponse>> listarTodos() {
        return horarioService.listarTodos();
    }

    @GET
    @Path("/profesional/{profesionalId}")
    @Operation(summary = "Listar horarios de un profesional específico")
    @APIResponse(responseCode = "200", description = "Lista de horarios del profesional")
    public Uni<List<HorarioDisponibleResponse>> listarPorProfesional(@PathParam("profesionalId") UUID profesionalId) {
        return horarioService.listarPorProfesional(profesionalId);
    }

    @POST
    @Operation(summary = "Registrar un horario disponible",
            description = "Registra un bloque de tiempo disponible para un profesional. " +
                    "No se permiten horarios solapados para el mismo profesional en la misma fecha.")
    @APIResponse(responseCode = "201", description = "Horario registrado exitosamente")
    @APIResponse(responseCode = "400", description = "Datos inválidos o hora fin anterior a hora inicio")
    @APIResponse(responseCode = "404", description = "Profesional no encontrado")
    @APIResponse(responseCode = "409", description = "El horario se solapa con uno existente")
    public Uni<Response> registrar(@Valid HorarioDisponibleRequest request) {
        return horarioService.registrar(request)
                .map(horario -> Response
                        .created(URI.create("/api/horarios/" + horario.id()))
                        .entity(horario)
                        .build());
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar un horario disponible")
    @APIResponse(responseCode = "204", description = "Horario eliminado exitosamente")
    @APIResponse(responseCode = "404", description = "Horario no encontrado")
    public Uni<Response> eliminar(@PathParam("id") UUID id) {
        return horarioService.eliminar(id)
                .map(v -> Response.noContent().build());
    }
}

