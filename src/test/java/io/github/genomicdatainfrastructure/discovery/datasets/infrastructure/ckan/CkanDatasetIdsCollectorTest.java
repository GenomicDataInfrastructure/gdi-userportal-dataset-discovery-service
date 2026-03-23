// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_DATASET_TYPE_FILTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence.CkanDatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;
import io.github.genomicdatainfrastructure.discovery.model.FilterType;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackage;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackageSearchRequest;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CkanDatasetIdsCollectorTest {

    @Test
    void collectAddsDatasetTypeFilterToFacetQuery() {
        var ckanQueryApi = mock(CkanQueryApi.class);
        when(ckanQueryApi.packageSearch(isNull(), any(PackageSearchRequest.class))).thenReturn(
                PackagesSearchResponse.builder()
                        .result(PackagesSearchResult.builder()
                                .count(1)
                                .results(List.of(CkanPackage.builder().identifier("dataset-1")
                                        .build()))
                                .build())
                        .build());

        var collector = new CkanDatasetIdsCollector(ckanQueryApi);
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("ckan")
                                .type(FilterType.DROPDOWN)
                                .key("tags")
                                .value("COVID")
                                .build()))
                .build();

        var ids = collector.collect(query, null);

        var requestCaptor = ArgumentCaptor.forClass(PackageSearchRequest.class);
        verify(ckanQueryApi).packageSearch(isNull(), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getFq())
                .isEqualTo("tags:(\"COVID\") AND " + CKAN_DATASET_TYPE_FILTER);
        assertThat(ids).containsOnlyKeys("dataset-1");
    }
}
