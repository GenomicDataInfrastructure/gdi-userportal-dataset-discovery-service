// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import io.github.genomicdatainfrastructure.discovery.datasets.domain.exceptions.DatasetNotFoundException;
import io.github.genomicdatainfrastructure.discovery.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DatasetNotFoundExceptionMapper implements
        ExceptionMapper<DatasetNotFoundException> {

    @Override
    public Response toResponse(DatasetNotFoundException exception) {
        var errorResponse = new ErrorResponse(
                "Dataset Not Found",
                NOT_FOUND.getStatusCode(),
                exception.getMessage()
        );

        return Response
                .status(NOT_FOUND)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
