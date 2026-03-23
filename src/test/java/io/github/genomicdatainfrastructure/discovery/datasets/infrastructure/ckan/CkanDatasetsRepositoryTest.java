// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_DATASET_TYPE_FILTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.mapper.CkanDatasetsMapper;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence.CkanDatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;
import io.github.genomicdatainfrastructure.discovery.model.FilterType;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackageSearchRequest;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResult;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CkanDatasetsRepositoryTest {

    @Test
    void searchAddsDatasetTypeFilterToFacetQuery() {
        var ckanQueryApi = mock(CkanQueryApi.class);
        var mapper = mock(CkanDatasetsMapper.class);
        when(ckanQueryApi.packageSearch(eq("en"), any(PackageSearchRequest.class))).thenReturn(
                PackagesSearchResponse.builder()
                        .result(PackagesSearchResult.builder().count(0).results(List.of()).build())
                        .build());
        when(mapper.map(any(PackagesSearchResult.class))).thenReturn(List.of());

        var repository = new CkanDatasetsRepository(ckanQueryApi, mapper);
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("ckan")
                                .type(FilterType.DROPDOWN)
                                .key("tags")
                                .value("COVID")
                                .build()))
                .build();

        repository.search(query, null, "en");

        var requestCaptor = ArgumentCaptor.forClass(PackageSearchRequest.class);
        verify(ckanQueryApi).packageSearch(eq("en"), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getFq())
                .isEqualTo("tags:(\"COVID\") AND " + CKAN_DATASET_TYPE_FILTER);
    }

    @Test
    void searchByDatasetIdsAddsDatasetTypeFilter() {
        var ckanQueryApi = mock(CkanQueryApi.class);
        var mapper = mock(CkanDatasetsMapper.class);
        when(ckanQueryApi.packageSearch(eq("en"), any(PackageSearchRequest.class))).thenReturn(
                PackagesSearchResponse.builder()
                        .result(PackagesSearchResult.builder().count(0).results(List.of()).build())
                        .build());
        when(mapper.map(any(PackagesSearchResult.class))).thenReturn(List.of());

        var repository = new CkanDatasetsRepository(ckanQueryApi, mapper);

        repository.search(Set.of("dataset-1"), null, 10, 0, null, "en");

        var requestCaptor = ArgumentCaptor.forClass(PackageSearchRequest.class);
        verify(ckanQueryApi).packageSearch(eq("en"), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getFq())
                .isEqualTo("identifier:(\"dataset-1\") AND " + CKAN_DATASET_TYPE_FILTER);
    }
}
