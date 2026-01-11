// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQueryParams;
import io.github.genomicdatainfrastructure.discovery.model.GVariantsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.api.GVariantsApi;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

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
    void givenNonEmptyQueryParams_whenSearch_thenReturnsMappedResponse() {
        GVariantSearchQuery query = new GVariantSearchQuery();
        GVariantSearchQueryParams params = new GVariantSearchQueryParams();
        params.setVariant("3:45864731:T:C");
        params.setRefGenome("GRCh37");
        query.setParams(params);

        BeaconResponse mockBeaconResponse = buildBeaconsResponse();

        when(gVariantsApi.postGenomicVariationsRequest(any()))
                .thenReturn(mockBeaconResponse);

        List<GVariantsSearchResponse> result = gVariantsRepository.search(query);

        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Result should not be empty");
        assertEquals(1, result.size(), "We expect 1 mapped variant response in this mock scenario");

        verify(gVariantsApi).postGenomicVariationsRequest(any());

        GVariantsSearchResponse item = result.getFirst();
        assertEquals("testBeaconId", item.getBeacon(), "Expected beacon ID from mock data");
        assertEquals(BigDecimal.valueOf(0.1234), item.getAlleleFrequency(),
                "Expected allele frequency from mock data");
    }
}