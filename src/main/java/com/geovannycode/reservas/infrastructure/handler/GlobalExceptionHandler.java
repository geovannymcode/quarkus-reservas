package com.geovannycode.reservas.infrastructure.handler;

import com.geovannycode.reservas.application.dto.response.ErrorResponse;
import com.geovannycode.reservas.domain.exception.BusinessException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Manejador global para excepciones de negocio ({@link BusinessException} y subclases).
 * Convierte las excepciones del dominio en respuestas HTTP estructuradas y consistentes.
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<BusinessException> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(BusinessException exception) {
        LOG.warnf("BusinessException capturada [%s]: %s",
                exception.getClass().getSimpleName(), exception.getMessage());

        var status = exception.getHttpStatus();
        var errorResponse = ErrorResponse.of(
                status.getStatusCode(),
                exception.getMessage(),
                uriInfo != null ? uriInfo.getPath() : "desconocida"
        );

        return Response.status(status)
                .entity(errorResponse)
                .build();
    }
}
