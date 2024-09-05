// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.facets.infrastructure.ckan;

import io.github.genomicdatainfrastructure.discovery.facets.ports.IFacetBuilder;
import io.github.genomicdatainfrastructure.discovery.model.Facet;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFacet;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResult;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Map;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_FACET_GROUP;
import static java.util.Optional.ofNullable;

@ApplicationScoped
public class CkanFacetBuilder implements IFacetBuilder {

    private static final String SELECTED_FACETS_PATTERN = "[\"%s\"]";

    private final CkanQueryApi ckanQueryApi;
    private final String selectedFacets;

    public CkanFacetBuilder(@RestClient CkanQueryApi ckanQueryApi,
            @ConfigProperty(name = "datasets.filters") String datasetFiltersAsString) {
        this.ckanQueryApi = ckanQueryApi;
        this.selectedFacets = SELECTED_FACETS_PATTERN.formatted(String.join("\",\"",
                datasetFiltersAsString.split(",")));
    }

    @Override
    public List<Facet> build(String accessToken) {
        var response = ckanQueryApi.packageSearch(
                null,
                null,
                null,
                null,
                null,
                selectedFacets,
                accessToken
        );

        var nonNullSearchFacets = ofNullable(response.getResult())
                .map(PackagesSearchResult::getSearchFacets)
                .orElseGet(Map::of);

        return facets(nonNullSearchFacets);
    }

    private List<Facet> facets(Map<String, CkanFacet> facets) {
        return facets
                .entrySet()
                .stream()
                .map(this::facet)
                .toList();
    }

    private Facet facet(Map.Entry<String, CkanFacet> entry) {
        var key = entry.getKey();
        var facet = entry.getValue();

        var values = ofNullable(facet.getItems())
                .orElseGet(List::of)
                .stream()
                .map(value -> ValueLabel.builder()
                        .value(value.getName())
                        .label(value.getDisplayName())
                        .build()
                )
                .toList();

        return Facet
                .builder()
                .facetGroup(CKAN_FACET_GROUP)
                .key(key)
                .label(facet.getTitle())
                .values(values)
                .build();
    }
}
