// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.ckan;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_DATASET_TYPE_FILTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.genomicdatainfrastructure.discovery.filters.infrastructure.mapper.CkanFilterMapper;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackageSearchRequest;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResult;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CkanFiltersRepositoryTest {

    @Test
    void getValuesForFilterAddsDatasetTypeConstraint() {
        var ckanQueryApi = mock(CkanQueryApi.class);
        var mapper = mock(CkanFilterMapper.class);
        when(ckanQueryApi.packageSearch(eq("en"), any(PackageSearchRequest.class))).thenReturn(
                PackagesSearchResponse.builder()
                        .result(PackagesSearchResult.builder().build())
                        .build());
        when(mapper.map(any(PackagesSearchResponse.class), eq("tags"))).thenReturn(List.of());

        var repository = new CkanFiltersRepository(ckanQueryApi, mapper);

        repository.getValuesForFilter("tags", "en");

        var requestCaptor = ArgumentCaptor.forClass(PackageSearchRequest.class);
        verify(ckanQueryApi).packageSearch(eq("en"), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getFq()).isEqualTo(CKAN_DATASET_TYPE_FILTER);
        assertThat(requestCaptor.getValue().getFacetField()).isEqualTo("[\"tags\"]");
    }
}
