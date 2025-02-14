// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.GVariantsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.github.genomicdatainfrastructure.discovery.model.GVariantsSearchResponse.PopulationEnum.FINLAND;
import static io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconRequestQuery.IncludeResultsetResponsesEnum.HIT;
import static io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconRequestQuery.RequestedGranularityEnum.RECORD;
import static org.junit.jupiter.api.Assertions.*;

class BeaconGVariantsRequestMapperTest {

    @Test
    @DisplayName("map(GVariantSearchQuery) should return a BeaconRequest with expected fields")
    void map_GVariantSearchQuery_ReturnsCorrectBeaconRequest() {
        GVariantSearchQuery query = new GVariantSearchQuery();

        query.setParams(Map.of("paramKey", "paramValue"));

        BeaconRequest result = BeaconGVariantsRequestMapper.map(query);

        assertNotNull(result, "BeaconRequest should not be null");
        assertNotNull(result.getMeta(), "BeaconRequest.meta should not be null");
        assertNotNull(result.getQuery(), "BeaconRequest.query should not be null");

        assertEquals(HIT, result.getQuery().getIncludeResultsetResponses(),
                "Expected includeResultsetResponses to be 'HIT'");
        assertEquals(RECORD, result.getQuery().getRequestedGranularity(),
                "Expected requestedGranularity to be 'RECORD'");
        assertFalse(result.getQuery().getTestMode(),
                "Expected testMode to default to false");
        assertNotNull(result.getQuery().getPagination(),
                "Pagination object should not be null");

        assertNotNull(result.getQuery().getFilters(),
                "Filters should not be null");
        assertTrue(result.getQuery().getFilters().isEmpty(),
                "Filters should be an empty list by default");

        assertEquals(query.getParams(), result.getQuery().getRequestParameters(),
                "Request parameters should match those from the GVariantSearchQuery");
    }

    @Test
    @DisplayName("map(BeaconResponse) should return a list of GVariantsSearchResponse with correct values")
    void map_BeaconResponse_ReturnsListOfGVariantsSearchResponse() {
        BeaconResponse beaconResponse = buildBeaconsResponse();

        List<GVariantsSearchResponse> result = BeaconGVariantsRequestMapper.map(beaconResponse);

        assertNotNull(result, "Resulting list should not be null");
        assertEquals(1, result.size(), "We expect exactly one result set in this mock response");

        GVariantsSearchResponse variant = result.getFirst();
        assertNotNull(variant, "GVariantsSearchResponse entry should not be null");

        assertEquals("testBeaconId", variant.getBeacon(),
                "Beacon ID should match the resultSet beacon ID");

        assertEquals(BigDecimal.valueOf(0.1234), variant.getAlleleFrequency(),
                "Allele frequency should match the mock frequency");

        assertEquals(FINLAND, variant.getPopulation(),
                "Population should be mapped from the input string 'fin'");
        assertEquals(BigDecimal.valueOf(100.0), variant.getAlleleCount(),
                "Allele count should match the mock frequency");
        assertEquals(BigDecimal.valueOf(200.0), variant.getAlleleNumber(),
                "Allele number should match the mock frequency");
        assertEquals(BigDecimal.valueOf(5.0), variant.getAlleleCountHomozygous(),
                "alleleCountHomozygous should match the mock frequency");
        assertEquals(BigDecimal.valueOf(95.0), variant.getAlleleCountHeterozygous(),
                "alleleCountHeterozygous should match the mock frequency");
    }

    public static BeaconResponse buildBeaconsResponse() {
        var freq = new Frequencies();
        freq.setPopulation("fin");
        freq.setAlleleFrequency(BigDecimal.valueOf(0.1234));
        freq.setAlleleCount(BigDecimal.valueOf(100.0));
        freq.setAlleleNumber(BigDecimal.valueOf(200.0));
        freq.setAlleleCountHomozygous(BigDecimal.valueOf(5.0));
        freq.setAlleleCountHeterozygous(BigDecimal.valueOf(95.0));

        var freqInPop = new FrequencyInPopulations();
        freqInPop.setFrequencies(Collections.singletonList(freq));

        var beaconResult = new Result();
        beaconResult.setFrequencyInPopulations(Collections.singletonList(freqInPop));

        var resultSet = new BeaconResultSet();
        resultSet.setBeaconId("testBeaconId");
        resultSet.setResults(Collections.singletonList(beaconResult));

        var responseContent = new BeaconResponseContent();
        responseContent.setResultSets(Collections.singletonList(resultSet));

        var responseData = new BeaconResponse();
        responseData.setResponse(responseContent);

        return responseData;
    }
}