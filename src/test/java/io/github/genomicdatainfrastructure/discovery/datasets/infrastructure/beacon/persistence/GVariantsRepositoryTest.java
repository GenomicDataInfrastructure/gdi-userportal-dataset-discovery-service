// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQueryParams;
import io.github.genomicdatainfrastructure.discovery.model.GVariantAlleleFrequencyResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.api.GVariantsApi;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.BeaconGVariantsRequestMapperTest.buildBeaconsResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GVariantsRepositoryTest {

    @Mock
    private GVariantsApi gVariantsApi;

    @InjectMocks
    private GVariantsRepository gVariantsRepository;

    @Test
    void givenEmptyQueryParams_whenSearch_thenReturnsEmptyResponse() {
        GVariantSearchQuery query = new GVariantSearchQuery();
        query.setParams(new GVariantSearchQueryParams());

        GVariantAlleleFrequencyResponse result = gVariantsRepository.search(query);

        assertNotNull(result);
        assertNull(result.getNumberOfPopulations(),
                "Result should have null numberOfPopulations when query params are empty");
        verify(gVariantsApi, never()).postGenomicVariationsRequest(any());
    }

    @Test
    void givenNonEmptyQueryParams_whenSearch_thenReturnsMappedResponse() {
        GVariantSearchQuery query = new GVariantSearchQuery();
        GVariantSearchQueryParams params = new GVariantSearchQueryParams();
        params.put("key1", "value1");
        query.setParams(params);

        BeaconResponse mockBeaconResponse = buildBeaconsResponse();

        when(gVariantsApi.postGenomicVariationsRequest(any()))
                .thenReturn(mockBeaconResponse);

        GVariantAlleleFrequencyResponse result = gVariantsRepository.search(query);

        assertNotNull(result, "Result should not be null");
        assertNotNull(result.getNumberOfPopulations(), "numberOfPopulations should not be null");
        assertEquals(1, result.getNumberOfPopulations(),
                "We expect 1 population in this mock scenario");
        assertEquals("The Genome of Europe", result.getSource(),
                "Expected source to be 'The Genome of Europe'");
        assertEquals("https://genomeofeurope.eu/", result.getSourceReference(),
                "Expected source reference URL");
        assertFalse(result.getPopulations().isEmpty(), "Populations list should not be empty");
        assertEquals(1, result.getPopulations().size(),
                "We expect 1 population response in this mock scenario");

        verify(gVariantsApi).postGenomicVariationsRequest(any());

        var population = result.getPopulations().getFirst();
        assertEquals("testBeaconId", population.getDatasetId(),
                "Expected dataset ID from mock data");
        assertEquals("0.1234", population.getAlleleFrequency(),
                "Expected allele frequency from mock data");
    }
}