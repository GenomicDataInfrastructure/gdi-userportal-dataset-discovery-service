// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.datasets.application.usecases.RetrieveDatasetInFormatQuery;
import io.github.genomicdatainfrastructure.discovery.datasets.application.usecases.RetrieveDatasetQuery;
import io.github.genomicdatainfrastructure.discovery.datasets.application.usecases.SearchDatasetsQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatasetQueryApiImpl implements DatasetQueryApi {

    private final SecurityIdentity identity;
    private final RetrieveDatasetInFormatQuery retrieveDatasetInFormatQuery;
    private final RetrieveDatasetQuery retrieveDatasetQuery;
    private final SearchDatasetsQuery searchDatasetsQuery;

    @Override
    public Response datasetSearch(DatasetSearchQuery datasetSearchQuery) {
        var content = searchDatasetsQuery.execute(datasetSearchQuery, accessToken());
        return Response.ok(content).build();
    }

    @Override
    public Response retrieveDataset(String id) {
        var content = retrieveDatasetQuery.execute(id, accessToken());
        return Response.ok(content).build();
    }

    @Override
    public Response retrieveDatasetInFormat(String id, String format) {
        var type = getType(format);
        var content = retrieveDatasetInFormatQuery.execute(id, format, accessToken());
        return Response
                .ok(content)
                .type(type)
                .header("Content-Disposition", "attachment; filename=\"" + id + "." + format + "\"")
                .build();
    }

    private String getType(String format) {
        return switch (format) {
            case "jsonld" -> "application/ld+json";
            case "rdf" -> "application/rdf+xml";
            case "ttl" -> "text/turtle";
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }

    private String accessToken() {
        if (identity.isAnonymous()) {
            return null;
        }
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        return principal.getRawToken();
    }
}
