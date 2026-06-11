// SPDX-FileCopyrightText: 2026 Health-RI
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.ckan;

import io.github.genomicdatainfrastructure.discovery.filters.infrastructure.quarkus.DatasetsConfig;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFilterHelpTextsResponse;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.java.Log;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

@Log
@ApplicationScoped
public class CkanFilterHelpTextService {

    private final CkanQueryApi ckanQueryApi;
    private final String configuredKeys;

    public CkanFilterHelpTextService(
            @RestClient CkanQueryApi ckanQueryApi,
            DatasetsConfig datasetsConfig
    ) {
        this.ckanQueryApi = ckanQueryApi;
        this.configuredKeys = toJsonArray(datasetsConfig.filters());
    }

    public List<Filter> enrich(List<Filter> filters, String preferredLanguage) {
        if (filters == null || filters.isEmpty()) {
            return filters;
        }

        var helpTexts = retrieveHelpTexts(preferredLanguage);
        if (helpTexts.isEmpty()) {
            return filters;
        }

        filters.forEach(filter -> filter.setHelpText(helpTexts.get(filter.getKey())));
        return filters;
    }

    private Map<String, String> retrieveHelpTexts(String preferredLanguage) {
        try {
            return Optional.ofNullable(ckanQueryApi.gdiFilterHelpTextsShow(
                    preferredLanguage,
                    configuredKeys
            ))
                    .map(CkanFilterHelpTextsResponse::getResult)
                    .orElseGet(Map::of);
        } catch (RuntimeException exception) {
            log.log(Level.WARNING, "Could not retrieve CKAN filter help texts: {0}", exception
                    .getMessage());
            return Map.of();
        }
    }

    private String toJsonArray(String filters) {
        var values = Optional.ofNullable(filters)
                .stream()
                .flatMap(value -> java.util.Arrays.stream(value.split(",")))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(value -> "\"" + value + "\"")
                .toList();

        return "[" + String.join(",", values) + "]";
    }
}
