// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.datasets.domain.exceptions.DatasetNotFoundException;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.mapper.CkanDatasetsMapper;
import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Set;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_FILTER_SOURCE;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_IDENTIFIER_FIELD;

@ApplicationScoped
public class CkanDatasetsRepository implements DatasetsRepository {

    private final CkanQueryApi ckanQueryApi;
    private final CkanDatasetsMapper ckanDatasetsMapper;

    @Inject
    public CkanDatasetsRepository(
            @RestClient CkanQueryApi ckanQueryApi, CkanDatasetsMapper ckanDatasetsMapper
    ) {
        this.ckanQueryApi = ckanQueryApi;
        this.ckanDatasetsMapper = ckanDatasetsMapper;
    }

    @Override
    public SearchResult search(
            Set<String> datasetIds,
            String sort,
            Integer rows,
            Integer start,
            String accessToken,
            String preferredLanguage) {

        if (datasetIds == null || datasetIds.isEmpty()) {
            return new SearchResult(0, List.of());
        }

        var facetsQuery = buildFacetQuery(datasetIds);

        var request = PackageSearchRequest.builder()
                .fq(facetsQuery)
                .sort(sort)
                .rows(rows)
                .start(start)
                .build();

        var response = ckanQueryApi.packageSearch(
                preferredLanguage,
                request
        );

        var mappedResults = ckanDatasetsMapper.map(response.getResult());
        var totalCount = response.getResult() != null && response.getResult().getCount() != null
                ? response.getResult().getCount()
                : mappedResults.size();
        return new SearchResult(totalCount, mappedResults);
    }

    @Override
    public RetrievedDataset findById(String id, String accessToken, String preferredLanguage) {
        try {
            var ckanPackage = ckanQueryApi.packageShow(id, preferredLanguage);
            return ckanDatasetsMapper.map(ckanPackage.getResult());
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new DatasetNotFoundException(id);
            }
            throw e;
        }
    }

    @Override
    public String retrieveDatasetInFormat(String id, String format, String accessToken) {
        try {
            return ckanQueryApi.retrieveDatasetInFormat(id, format, accessToken);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new DatasetNotFoundException(id);
            }
            throw e;
        }
    }

    private String buildFacetQuery(Set<String> datasetIds) {
        var facets = datasetIds
                .stream()
                .map(id -> DatasetSearchQueryFacet
                        .builder()
                        .source(CKAN_FILTER_SOURCE)
                        .key(CKAN_IDENTIFIER_FIELD)
                        .value(id)
                        .build())
                .toList();

        return CkanFacetsQueryBuilder.buildFacetQuery(DatasetSearchQuery
                .builder()
                .facets(facets)
                .build());
    }
}
