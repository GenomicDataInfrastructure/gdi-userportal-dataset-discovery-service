// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQueryParams;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.api.GVariantsApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.BeaconGVariantsRequestMapperTest.buildBeaconsResponse;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.BeaconGVariantsRequestMapperTest.buildBeaconsResponseWithPopulation;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GVariantsRepositoryTest {

    @Mock
    private GVariantsApi gVariantsApi;

    @InjectMocks
    private GVariantsRepository gVariantsRepository;

    @Test
    void givenNonEmptyQueryParams_whenSearch_thenReturnsMappedResponse() {
        var query = createQuery("3:45864731:T:C", "GRCh37", null, null);
        when(gVariantsApi.postGenomicVariationsRequest(any())).thenReturn(buildBeaconsResponse());

        var result = gVariantsRepository.search(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testBeaconId", result.getFirst().getBeacon());
    }

    @Test
    void givenCombinedFilters_whenSearch_thenFiltersExactMatch() {
        var query = createQuery("3:45864731:T:C", "GRCh37", "FR", "M");
        when(gVariantsApi.postGenomicVariationsRequest(any())).thenReturn(
                buildBeaconsResponseWithPopulation("FR_M"));

        var result = gVariantsRepository.search(query);

        assertEquals(1, result.size());
    }

    @Test
    void givenNullParams_whenSearch_thenReturnsEmptyList() {
        var query = new GVariantSearchQuery();
        query.setParams(null);

        var result = gVariantsRepository.search(query);

        assertTrue(result.isEmpty());
        verify(gVariantsApi, never()).postGenomicVariationsRequest(any());
    }

    private GVariantSearchQuery createQuery(String variant, String refGenome, String country,
            String sex) {
        var query = new GVariantSearchQuery();
        var params = new GVariantSearchQueryParams();

        if (variant != null) {
            String[] parts = variant.split(":");
            if (parts.length == 4) {
                params.setReferenceName(parts[0]);
                params.setStart(java.util.List.of(Integer.parseInt(parts[1])));
                params.setReferenceBases(parts[2]);
                params.setAlternateBases(parts[3]);
            }
        }

        params.setAssemblyId(refGenome);
        params.setCountryOfBirth(country);
        params.setSex(sex);
        query.setParams(params);
        return query;
    }
}
