// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * CKAN exposes DCAT exports for both datasets and dataset series.
 * Dataset series exports live under {@code /dataset_series/{id}.{format}}.
 */
@RegisterRestClient(configKey = "ckan_yaml")
@Path("/")
public interface CkanDatasetSeriesExportApi {

    @GET
    @Path("/dataset_series/{id}.{format}")
    @Produces({
            "application/ld+json",
            "application/rdf+xml",
            "text/turtle"
    })
    String retrieveDatasetSeriesInFormat(
            @PathParam("id") String id,
            @PathParam("format") String format,
            @HeaderParam("Authorization") String authorization
    );
}
