package com.geovannycode.reservas.infrastructure.handler;

import com.geovannycode.reservas.application.dto.response.ErrorResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Manejador para IllegalArgumentException (errores de argumento inválido en servicios).
 * Retorna HTTP 400 Bad Request con el mensaje descriptivo.
 */
@Provider
public class IllegalArgumentExceptionHandler implements ExceptionMapper<IllegalArgumentException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        var errorResponse = ErrorResponse.of(
                400,
                exception.getMessage(),
                uriInfo != null ? uriInfo.getPath() : "desconocida"
        );

        return Response.status(400)
                .entity(errorResponse)
                .build();
    }
}

