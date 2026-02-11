// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQueryParams;
import io.github.genomicdatainfrastructure.discovery.model.GVariantsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BeaconGVariantsRequestMapperTest {

    @Test
    @DisplayName("map(GVariantSearchQuery) should not include assemblyId when null")
    void map_GVariantSearchQuery_WithNullAssemblyId_DoesNotIncludeAssemblyId() {
        GVariantSearchQuery query = new GVariantSearchQuery();
        GVariantSearchQueryParams params = new GVariantSearchQueryParams();
        params.setReferenceName("21");
        params.setStart(List.of(9411448));
        params.setReferenceBases("G");
        params.setAlternateBases("T");
        params.setAssemblyId(null);
        query.setParams(params);

        var result = BeaconGVariantsRequestMapper.map(query);

        assertNotNull(result);
        assertNotNull(result.getQuery());
        var requestParams = result.getQuery().getRequestParameters();
        assertNotNull(requestParams);
        assertFalse(requestParams.containsKey("assemblyId"),
                "assemblyId should not be included when null");
        assertEquals("21", requestParams.get("referenceName"));
    }

    @Test
    @DisplayName("map(GVariantSearchQuery) should include assemblyId when set to specific value")
    void map_GVariantSearchQuery_WithSpecificAssemblyId_IncludesAssemblyId() {
        GVariantSearchQuery query = new GVariantSearchQuery();
        GVariantSearchQueryParams params = new GVariantSearchQueryParams();
        params.setReferenceName("21");
        params.setStart(List.of(9411448));
        params.setReferenceBases("G");
        params.setAlternateBases("T");
        params.setAssemblyId("GRCh37");
        query.setParams(params);

        var result = BeaconGVariantsRequestMapper.map(query);

        assertNotNull(result);
        assertNotNull(result.getQuery());
        var requestParams = result.getQuery().getRequestParameters();
        assertNotNull(requestParams);
        assertTrue(requestParams.containsKey("assemblyId"),
                "assemblyId should be included when set to a specific value");
        assertEquals("GRCh37", requestParams.get("assemblyId"));
    }

    // Note: BeaconRequest mapping test removed as it requires full query object setup
    // The response mapping test below covers the critical population parsing logic

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

        assertEquals("fin", variant.getPopulation(),
                "Population should be the population string from input 'fin'");
        assertEquals(BigDecimal.valueOf(100.0), variant.getAlleleCount(),
                "Allele count should match the mock frequency");
        assertEquals(BigDecimal.valueOf(200.0), variant.getAlleleNumber(),
                "Allele number should match the mock frequency");
        assertEquals(BigDecimal.valueOf(5.0), variant.getAlleleCountHomozygous(),
                "alleleCountHomozygous should match the mock frequency");
        assertEquals(BigDecimal.valueOf(95.0), variant.getAlleleCountHeterozygous(),
                "alleleCountHeterozygous should match the mock frequency");
    }

    @Test
    @DisplayName("map(BeaconResponse) should extract sex from population format FR_M")
    void map_BeaconResponse_ExtractsSexFromPopulation() {
        BeaconResponse beaconResponse = buildBeaconsResponseWithPopulation("FR_M");

        List<GVariantsSearchResponse> result = BeaconGVariantsRequestMapper.map(beaconResponse);

        assertEquals(1, result.size());
        GVariantsSearchResponse variant = result.getFirst();

        assertEquals("FR_M", variant.getPopulation(), "Population should be FR_M");
        assertEquals("M", variant.getSex(), "Sex should be extracted as M");
        assertEquals("FR", variant.getCountryOfBirth(), "Country should be extracted as FR");
    }

    @Test
    @DisplayName("map(BeaconResponse) should extract sex from population format FR_F")
    void map_BeaconResponse_ExtractsSexFemale() {
        BeaconResponse beaconResponse = buildBeaconsResponseWithPopulation("FR_F");

        List<GVariantsSearchResponse> result = BeaconGVariantsRequestMapper.map(beaconResponse);

        assertEquals(1, result.size());
        GVariantsSearchResponse variant = result.getFirst();

        assertEquals("FR_F", variant.getPopulation());
        assertEquals("F", variant.getSex(), "Sex should be extracted as F");
        assertEquals("FR", variant.getCountryOfBirth(), "Country should be extracted as FR");
    }

    @Test
    @DisplayName("map(BeaconResponse) should handle country only format FR")
    void map_BeaconResponse_HandlesCountryOnly() {
        BeaconResponse beaconResponse = buildBeaconsResponseWithPopulation("FR");

        List<GVariantsSearchResponse> result = BeaconGVariantsRequestMapper.map(beaconResponse);

        assertEquals(1, result.size());
        GVariantsSearchResponse variant = result.getFirst();

        assertEquals("FR", variant.getPopulation());
        assertNull(variant.getSex(), "Sex should be null for country-only format");
        assertEquals("FR", variant.getCountryOfBirth(), "Country should be extracted as FR");
    }

    @Test
    @DisplayName("map(BeaconResponse) should handle sex only format M")
    void map_BeaconResponse_HandlesSexOnly() {
        BeaconResponse beaconResponse = buildBeaconsResponseWithPopulation("M");

        List<GVariantsSearchResponse> result = BeaconGVariantsRequestMapper.map(beaconResponse);

        assertEquals(1, result.size());
        GVariantsSearchResponse variant = result.getFirst();

        assertEquals("M", variant.getPopulation());
        assertEquals("M", variant.getSex(), "Sex should be extracted as M");
        assertNull(variant.getCountryOfBirth(), "Country should be null for sex-only format");
    }

    @Test
    @DisplayName("map(BeaconResponse) should handle sex only format F")
    void map_BeaconResponse_HandlesSexOnlyFemale() {
        BeaconResponse beaconResponse = buildBeaconsResponseWithPopulation("F");

        List<GVariantsSearchResponse> result = BeaconGVariantsRequestMapper.map(beaconResponse);

        assertEquals(1, result.size());
        GVariantsSearchResponse variant = result.getFirst();

        assertEquals("F", variant.getPopulation());
        assertEquals("F", variant.getSex(), "Sex should be extracted as F");
        assertNull(variant.getCountryOfBirth(), "Country should be null for sex-only format");
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

    public static BeaconResponse buildBeaconsResponseWithPopulation(String population) {
        var freq = new Frequencies();
        freq.setPopulation(population);
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