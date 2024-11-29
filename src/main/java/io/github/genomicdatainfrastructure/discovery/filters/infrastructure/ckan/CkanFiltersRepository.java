// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.ckan;

import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FiltersRepository;
import io.github.genomicdatainfrastructure.discovery.filters.infrastructure.mapper.CkanFilterMapper;
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
    private final CkanFilterMapper ckanFilterMapper;

    public CkanFiltersRepository(@RestClient CkanQueryApi ckanQueryApi,
            CkanFilterMapper ckanFilterMapper) {
        this.ckanQueryApi = ckanQueryApi;

        this.ckanFilterMapper = ckanFilterMapper;
    }

    @Override
    public List<ValueLabel> getValuesForFilter(final String key) {

        final var facetField = SELECTED_FACETS_PATTERN.formatted(key);

        final var request = PackageSearchRequest.builder()
                .facetField(facetField)
                .rows(0)
                .facetLimit(-1)
                .build();

        final var response = ckanQueryApi.packageSearch(
                request
        );
        return ckanFilterMapper.map(response, key);
    }
}
