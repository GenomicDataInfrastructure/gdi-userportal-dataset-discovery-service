// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.ckan;

import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FilterBuilder;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackageSearchRequest;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_FILTER_SOURCE;

@ApplicationScoped
public class CkanFilterBuilder implements FilterBuilder {

    private final CkanQueryApi ckanQueryApi;
    private final CkanSearchFacetsMapper ckanSearchFacetsMapper;

    public CkanFilterBuilder(
            @RestClient CkanQueryApi ckanQueryApi,
            CkanSearchFacetsMapper ckanSearchFacetsMapper
    ) {
        this.ckanQueryApi = ckanQueryApi;
        this.ckanSearchFacetsMapper = ckanSearchFacetsMapper;
    }

    @Override
    public String source() {
        return CKAN_FILTER_SOURCE;
    }

    @Override
    public List<Filter> build(String accessToken, String preferredLanguage) {
        var request = PackageSearchRequest.builder()
                .rows(0)
                .start(0)
                .facetLimit(-1)
                .facetField(ckanSearchFacetsMapper.selectedFacetField())
                .build();

        var response = ckanQueryApi.packageSearch(
                preferredLanguage,
                request
        );

        return ckanSearchFacetsMapper.map(response.getResult());
    }
}
