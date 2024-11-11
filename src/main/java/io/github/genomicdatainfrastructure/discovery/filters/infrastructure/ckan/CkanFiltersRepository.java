// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.ckan;

import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FiltersRepository;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFacet;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackageSearchRequest;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResult;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@ApplicationScoped
public class CkanFiltersRepository implements FiltersRepository {

    private static final String SELECTED_FACETS_PATTERN = "[\"%s\"]";

    private final CkanQueryApi ckanQueryApi;

    public CkanFiltersRepository(@RestClient CkanQueryApi ckanQueryApi) {
        this.ckanQueryApi = ckanQueryApi;

    }

    @Override
    public List<ValueLabel> getValuesForFilter(String key) {

        var facetField = SELECTED_FACETS_PATTERN.formatted(key);

        var request = PackageSearchRequest.builder()
                .facetField(facetField)
                .rows(0)
                .facetLimit(-1)
                .build();

        var response = ckanQueryApi.packageSearch(
                request
        );

        var ckanFacet = ofNullable(response.getResult())
                .map(PackagesSearchResult::getSearchFacets)
                .orElseGet(Map::of)
                .get(key);
        if (ckanFacet == null) {
            return List.of();
        }
        return filters(ckanFacet);

    }

    private List<ValueLabel> filters(CkanFacet facet) {

        var values = ofNullable(facet.getItems())
                .orElseGet(List::of)
                .stream()
                .map(value -> ValueLabel.builder()
                        .value(value.getName())
                        .label(value.getDisplayName())
                        .count(value.getCount())
                        .build()
                )
                .toList();

        return values;
    }

}
