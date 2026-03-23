// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.ckan;

import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FiltersRepository;
import io.github.genomicdatainfrastructure.discovery.filters.infrastructure.mapper.CkanFilterMapper;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackageSearchRequest;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.withDatasetTypeFilter;

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
    public List<ValueLabel> getValuesForFilter(final String key, String preferredLanguage) {

        final var facetField = SELECTED_FACETS_PATTERN.formatted(key);

        final var request = PackageSearchRequest.builder()
                .facetField(facetField)
                .rows(0)
                .facetLimit(-1)
                .fq(withDatasetTypeFilter(null))
                .build();

        final var response = ckanQueryApi.packageSearch(
                preferredLanguage,
                request
        );
        return ckanFilterMapper.map(response, key);
    }
}
