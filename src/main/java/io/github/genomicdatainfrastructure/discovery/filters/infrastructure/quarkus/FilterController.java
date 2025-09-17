// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.quarkus;

import io.github.genomicdatainfrastructure.discovery.api.FiltersQueryApi;
import io.github.genomicdatainfrastructure.discovery.filters.application.usecases.RetrieveFiltersQuery;
import io.github.genomicdatainfrastructure.discovery.filters.application.usecases.RetrieveFiltersValuesQuery;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import lombok.RequiredArgsConstructor;

import static io.github.genomicdatainfrastructure.discovery.api.DatasetQueryApiImpl.getString;

@RequiredArgsConstructor
public class FilterController implements FiltersQueryApi {

    private final SecurityIdentity identity;
    private final RetrieveFiltersQuery query;
    private final RetrieveFiltersValuesQuery valuesQuery;

    @ConfigProperty(name = "app.accept-language.default", defaultValue = "")
    String defaultAcceptLanguage;

    @Context
    HttpHeaders headers;

    @Override
    public Response retrieveFilters(String acceptLanguage) {
        var preferredLanguage = preferredLanguage();
        var facets = query.execute(accessToken(), preferredLanguage);
        return Response.ok(facets).build();
    }

    private String accessToken() {
        if (identity.isAnonymous()) {
            return null;
        }
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        return principal.getRawToken();
    }

    @Override
    public Response retrieveFilterValues(String key, String acceptLanguage) {
        var preferredLanguage = preferredLanguage();
        var values = valuesQuery.execute(key, preferredLanguage);
        return Response.ok(values).build();
    }

    private String preferredLanguage() {
        return getString(headers, defaultAcceptLanguage);
    }

}
