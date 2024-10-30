// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.datasets.domain.exceptions.InvalidFacetException;
import io.github.genomicdatainfrastructure.discovery.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

@Provider
public class InvalidFacetExceptionMapper implements ExceptionMapper<InvalidFacetException> {

    @Override
    public Response toResponse(InvalidFacetException exception) {
        var errorResponse = new ErrorResponse(
                "Invalid Facet",
                BAD_REQUEST.getStatusCode(),
                exception.getMessage()
        );

        return Response
                .status(BAD_REQUEST)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
