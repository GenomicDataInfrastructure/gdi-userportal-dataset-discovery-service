// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackage;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackageSearchRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_PAGINATION_MAX_SIZE;

@ApplicationScoped
public class CkanDatasetIdsCollector implements DatasetIdsCollector {

    private final CkanQueryApi ckanQueryApi;

    @Inject
    public CkanDatasetIdsCollector(
            @RestClient CkanQueryApi ckanQueryApi
    ) {
        this.ckanQueryApi = ckanQueryApi;
    }

    @Override
    public Map<String, Integer> collect(DatasetSearchQuery query, String accessToken) {
        var facetsQuery = CkanFacetsQueryBuilder.buildFacetQuery(query);

        var request = new PackageSearchRequest(
                query.getQuery(),
                facetsQuery,
                null,
                CKAN_PAGINATION_MAX_SIZE,
                0,
                null);

        var response = ckanQueryApi.packageSearch(
                request
        );

        var datasetIdsByRecordCount = new HashMap<String, Integer>();

        response.getResult()
                .getResults()
                .stream()
                .map(CkanPackage::getIdentifier)
                .filter(Objects::nonNull)
                .forEach(id -> datasetIdsByRecordCount.put(id, null));

        return datasetIdsByRecordCount;
    }
}
