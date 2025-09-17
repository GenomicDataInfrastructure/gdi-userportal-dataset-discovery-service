// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.common.AcceptLanguageParser;
import io.github.genomicdatainfrastructure.discovery.datasets.application.usecases.RetrieveDatasetInFormatQuery;
import io.github.genomicdatainfrastructure.discovery.datasets.application.usecases.RetrieveDatasetQuery;
import io.github.genomicdatainfrastructure.discovery.datasets.application.usecases.SearchDatasetsQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.common.constraint.Nullable;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
public class DatasetQueryApiImpl implements DatasetQueryApi {

    private final SecurityIdentity identity;
    private final RetrieveDatasetInFormatQuery retrieveDatasetInFormatQuery;
    private final RetrieveDatasetQuery retrieveDatasetQuery;
    private final SearchDatasetsQuery searchDatasetsQuery;

    @Context
    HttpHeaders headers;

    @ConfigProperty(name = "app.accept-language.default", defaultValue = "en")
    String defaultAcceptLanguage;

    @Override
    public Response datasetSearch(String acceptLanguage, DatasetSearchQuery datasetSearchQuery) {
        var preferredLanguage = preferredLanguage();
        var content = searchDatasetsQuery.execute(datasetSearchQuery, accessToken(),
                preferredLanguage);
        return Response.ok(content).build();
    }

    @Override
    public Response retrieveDataset(String id, String acceptLanguage) {
        var content = retrieveDatasetQuery.execute(id, accessToken(), preferredLanguage());
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

    private String preferredLanguage() {
        return getString(headers, defaultAcceptLanguage);
    }

    @Nullable
    public static String getString(HttpHeaders headers, String defaultAcceptLanguage) {
        List<Locale> languages = headers.getAcceptableLanguages();
        if (languages != null && !languages.isEmpty()) {
            return languages.getFirst().toLanguageTag().toLowerCase(Locale.ROOT);
        }
        return (defaultAcceptLanguage == null || defaultAcceptLanguage.isBlank())
                ? null
                : defaultAcceptLanguage;
    }
}
