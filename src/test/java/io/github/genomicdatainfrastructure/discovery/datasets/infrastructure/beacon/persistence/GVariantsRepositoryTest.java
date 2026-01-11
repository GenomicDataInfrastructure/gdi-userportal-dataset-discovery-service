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
    void givenEmptyFilters_whenSearch_thenReturnsAllVariants() {
        var query = createQuery("3:45864731:T:C", "GRCh37", null, null);
        when(gVariantsApi.postGenomicVariationsRequest(any())).thenReturn(buildBeaconsResponse());

        var result = gVariantsRepository.search(query);

        assertEquals(1, result.size());
    }

    @Test
    void givenCountryFilter_whenSearch_thenFiltersCorrectly() {
        var query = createQuery("3:45864731:T:C", "GRCh37", "FI", null);
        when(gVariantsApi.postGenomicVariationsRequest(any())).thenReturn(
                buildBeaconsResponseWithPopulation("FI"));

        var result = gVariantsRepository.search(query);

        assertEquals(1, result.size());
    }

    @Test
    void givenInvalidPopulation_whenSearch_thenFiltersOut() {
        var query = createQuery("3:45864731:T:C", "GRCh37", "FI", null);
        when(gVariantsApi.postGenomicVariationsRequest(any())).thenReturn(
                buildBeaconsResponseWithPopulation("INVALID"));

        var result = gVariantsRepository.search(query);

        assertTrue(result.isEmpty());
    }

    private GVariantSearchQuery createQuery(String variant, String refGenome, String country,
            String sex) {
        var query = new GVariantSearchQuery();
        var params = new GVariantSearchQueryParams();
        params.setVariant(variant);
        params.setRefGenome(refGenome);
        params.setCountryOfBirth(country);
        params.setSex(sex);
        query.setParams(params);
        return query;
    }
}