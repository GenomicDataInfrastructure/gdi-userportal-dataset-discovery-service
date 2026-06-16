// SPDX-FileCopyrightText: 2026 Health-RI
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.ckan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFilterHelpTextsResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

@Log
@ApplicationScoped
public class CkanFilterHelpTextService {

    private final CkanQueryApi ckanQueryApi;
    private final ObjectMapper objectMapper;

    public CkanFilterHelpTextService(@RestClient CkanQueryApi ckanQueryApi,
            ObjectMapper objectMapper) {
        this.ckanQueryApi = ckanQueryApi;
        this.objectMapper = objectMapper;
    }

    public List<Filter> enrich(List<Filter> filters, String preferredLanguage) {
        if (filters == null || filters.isEmpty()) {
            return filters;
        }

        var helpTexts = retrieveHelpTexts(filters, preferredLanguage);
        if (helpTexts.isEmpty()) {
            return filters;
        }

        filters.forEach(filter -> filter.setHelpText(normalizeHelpText(helpTexts.get(filter
                .getKey()))));
        return filters;
    }

    private Map<String, String> retrieveHelpTexts(List<Filter> filters, String preferredLanguage) {
        try {
            var keys = toJsonArray(filters);
            return Optional.ofNullable(ckanQueryApi.gdiFilterHelpTextsShow(
                    preferredLanguage,
                    keys
            ))
                    .map(CkanFilterHelpTextsResponse::getResult)
                    .orElseGet(Map::of);
        } catch (WebApplicationException | ProcessingException exception) {
            log.log(Level.WARNING, "Could not retrieve CKAN filter help texts", exception);
            return Map.of();
        }
    }

    private String toJsonArray(List<Filter> filters) {
        var keys = Optional.ofNullable(filters)
                .orElseGet(List::of)
                .stream()
                .map(Filter::getKey)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .toList();

        try {
            return objectMapper.writeValueAsString(keys);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize filter keys for CKAN", exception);
        }
    }

    private String normalizeHelpText(String helpText) {
        var normalizedHelpText = StringUtils.normalizeSpace(helpText);
        return StringUtils.isBlank(normalizedHelpText) ? null : normalizedHelpText;
    }
}
