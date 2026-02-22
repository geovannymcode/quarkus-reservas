package com.geovannycode.reservas.infrastructure.resource;

import com.geovannycode.reservas.application.dto.request.ClienteRequest;
import com.geovannycode.reservas.application.dto.response.ClienteResponse;
import com.geovannycode.reservas.application.service.ClienteService;
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
 * Recurso REST para la gestión de Clientes.
 * Expone el CRUD completo con validación de unicidad de email.
 */
@Path("/api/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Clientes", description = "Gestión de clientes del centro de servicios")
public class ClienteResource {

    private final ClienteService clienteService;

    public ClienteResource(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GET
    @Operation(summary = "Listar todos los clientes")
    @APIResponse(responseCode = "200", description = "Lista de clientes")
    public Uni<List<ClienteResponse>> listarTodos() {
        return clienteService.listarTodos();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    @APIResponse(responseCode = "200", description = "Cliente encontrado")
    @APIResponse(responseCode = "404", description = "Cliente no encontrado")
    public Uni<Response> buscarPorId(@PathParam("id") UUID id) {
        return clienteService.buscarPorId(id)
                .map(cliente -> Response.ok(cliente).build());
    }

    @POST
    @Operation(summary = "Crear un nuevo cliente",
            description = "El email debe ser único en el sistema")
    @APIResponse(responseCode = "201", description = "Cliente creado exitosamente")
    @APIResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @APIResponse(responseCode = "409", description = "El email ya está registrado")
    public Uni<Response> crear(@Valid ClienteRequest request) {
        return clienteService.crear(request)
                .map(cliente -> Response
                        .created(URI.create("/api/clientes/" + cliente.id()))
                        .entity(cliente)
                        .build());
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar datos de un cliente")
    @APIResponse(responseCode = "200", description = "Cliente actualizado exitosamente")
    @APIResponse(responseCode = "404", description = "Cliente no encontrado")
    @APIResponse(responseCode = "409", description = "El email ya está registrado por otro cliente")
    public Uni<Response> actualizar(@PathParam("id") UUID id, @Valid ClienteRequest request) {
        return clienteService.actualizar(id, request)
                .map(cliente -> Response.ok(cliente).build());
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Eliminar un cliente")
    @APIResponse(responseCode = "204", description = "Cliente eliminado exitosamente")
    @APIResponse(responseCode = "404", description = "Cliente no encontrado")
    public Uni<Response> eliminar(@PathParam("id") UUID id) {
        return clienteService.eliminar(id)
                .map(v -> Response.noContent().build());
    }
}
