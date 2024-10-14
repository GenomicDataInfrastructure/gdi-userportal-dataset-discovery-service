// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.quarkus;

import io.github.genomicdatainfrastructure.discovery.api.FiltersQueryApi;
import io.github.genomicdatainfrastructure.discovery.filters.application.usecases.RetrieveFiltersQuery;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FilterController implements FiltersQueryApi {

    private final SecurityIdentity identity;
    private final RetrieveFiltersQuery query;

    @Override
    public Response retrieveFilters() {
        var facets = query.execute(accessToken());
        return Response.ok(facets).build();
    }

    private String accessToken() {
        if (identity.isAnonymous()) {
            return null;
        }
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        return principal.getRawToken();
    }

}
