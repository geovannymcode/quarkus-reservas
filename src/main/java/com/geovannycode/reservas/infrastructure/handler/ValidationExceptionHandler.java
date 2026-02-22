package com.geovannycode.reservas.infrastructure.handler;

import com.geovannycode.reservas.application.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.stream.Collectors;

/**
 * Manejador para excepciones de validación de Bean Validation (@Valid).
 * Consolida todos los mensajes de error en una respuesta HTTP 400 estructurada.
 */
@Provider
public class ValidationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger LOG = Logger.getLogger(ValidationExceptionHandler.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        var errores = exception.getConstraintViolations().stream()
                .map(cv -> {
                    var field = cv.getPropertyPath().toString();
                    // Extraer solo el nombre del campo sin el prefijo del método
                    var fieldName = field.contains(".") ? field.substring(field.lastIndexOf('.') + 1) : field;
                    return String.format("%s: %s", fieldName, cv.getMessage());
                })
                .sorted()
                .collect(Collectors.joining("; "));

        LOG.debugf("Error de validación: %s", errores);

        var errorResponse = ErrorResponse.of(
                400,
                "Errores de validación: " + errores,
                uriInfo != null ? uriInfo.getPath() : "desconocida"
        );

        return Response.status(400)
                .entity(errorResponse)
                .build();
    }
}
