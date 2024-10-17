// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.ckan;

import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FilterBuilder;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.model.Filter.TypeEnum;
import io.github.genomicdatainfrastructure.discovery.model.FilterInputs;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFacet;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackageSearchRequest;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResult;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Map;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_FACET_GROUP;
import static java.util.Optional.ofNullable;

@ApplicationScoped
public class CkanFilterBuilder implements FilterBuilder {

    private static final String SELECTED_FACETS_PATTERN = "[\"%s\"]";

    private final CkanQueryApi ckanQueryApi;
    private final String selectedFacets;

    public CkanFilterBuilder(@RestClient CkanQueryApi ckanQueryApi,
            @ConfigProperty(name = "datasets.filters") String datasetFiltersAsString) {
        this.ckanQueryApi = ckanQueryApi;
        this.selectedFacets = SELECTED_FACETS_PATTERN.formatted(String.join("\",\"",
                datasetFiltersAsString.split(",")));
    }

    @Override
    public List<Filter> build(String accessToken) {
        var request = new PackageSearchRequest(
                null,
                null,
                null,
                0,
                0,
                selectedFacets);

        var response = ckanQueryApi.packageSearch(
                request
        );

        var nonNullSearchFacets = ofNullable(response.getResult())
                .map(PackagesSearchResult::getSearchFacets)
                .orElseGet(Map::of);

        return filters(nonNullSearchFacets);
    }

    private List<Filter> filters(Map<String, CkanFacet> filters) {
        return filters
                .entrySet()
                .stream()
                .map(this::filter)
                .toList();
    }

    private Filter filter(Map.Entry<String, CkanFacet> entry) {
        var key = entry.getKey();
        var filter = entry.getValue();

        var values = ofNullable(filter.getItems())
                .orElseGet(List::of)
                .stream()
                .map(value -> ValueLabel.builder()
                        .value(value.getName())
                        .label(value.getDisplayName())
                        .build()
                )
                .toList();

        return Filter
                .builder()
                .source(CKAN_FACET_GROUP)
                .type(TypeEnum.DROPDOWN)
                .key(key)
                .label(filter.getTitle())
                .inputs(FilterInputs
                        .builder()
                        .values(values)
                        .build())
                .build();
    }
}
