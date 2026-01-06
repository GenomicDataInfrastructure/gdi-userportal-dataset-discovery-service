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
import java.util.List;
import java.util.Optional;

import static io.github.genomicdatainfrastructure.discovery.model.GVariantsSearchResponse.PopulationEnum.FINLAND;
import static io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconRequestQuery.IncludeResultsetResponsesEnum.HIT;
import static io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconRequestQuery.RequestedGranularityEnum.RECORD;
import static org.junit.jupiter.api.Assertions.*;

class BeaconGVariantsRequestMapperTest {

    @Test
    @DisplayName("map(GVariantSearchQuery) should return a BeaconRequest with expected fields")
    void map_GVariantSearchQuery_ReturnsCorrectBeaconRequest() {
        GVariantSearchQuery query = new GVariantSearchQuery();
        GVariantSearchQueryParams params = new GVariantSearchQueryParams();
        params.put("paramKey", "paramValue");
        query.setParams(params);

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

        assertEquals(1, result.getQuery().getRequestParameters().size(),
                "Request parameters should contain only the non-local parameters");
        assertTrue(result.getQuery().getRequestParameters().containsKey("paramKey"),
                "Request parameters should contain the provided paramKey");
        assertEquals("paramValue", result.getQuery().getRequestParameters().get("paramKey"),
                "Request parameters should preserve the provided param value");
    }

    @Test
    @DisplayName("map(GVariantSearchQuery) should return a BeaconRequest with expected fields and strip local-only params")
    void map_GVariantSearchQuery_ReturnsCorrectBeaconRequest_StripsLocalParams() {
        GVariantSearchQuery query = new GVariantSearchQuery();
        GVariantSearchQueryParams params = new GVariantSearchQueryParams();
        params.put("paramKey", "paramValue");
        params.put("sex", "male");
        params.put("countryOfBirth", "FI");
        query.setParams(params);

        BeaconRequest result = BeaconGVariantsRequestMapper.map(query);

        assertTrue(result.getQuery().getFilters().isEmpty(),
                "Filters should be an empty list by default");

        assertEquals(1, result.getQuery().getRequestParameters().size(),
                "Only non-local parameters should be present in requestParameters");
        assertTrue(result.getQuery().getRequestParameters().containsKey("paramKey"),
                "Normal parameter should be preserved in requestParameters");
        assertFalse(result.getQuery().getRequestParameters().containsKey("sex"),
                "Local-only parameter 'sex' should be stripped from requestParameters");
        assertFalse(result.getQuery().getRequestParameters().containsKey("countryOfBirth"),
                "Local-only parameter 'countryOfBirth' should be stripped from requestParameters");
    }

    @Test
    @DisplayName("extractPopulationFilter should handle various cases")
    void extractPopulationFilter_HandleCases() {
        // Case 1: Null query
        assertTrue(BeaconGVariantsRequestMapper.extractPopulationFilter(null).isEmpty());

        // Case 2: Null params
        GVariantSearchQuery queryWithoutParams = new GVariantSearchQuery();
        assertTrue(BeaconGVariantsRequestMapper.extractPopulationFilter(queryWithoutParams)
                .isEmpty());

        // Case 3: Only sex set
        GVariantSearchQuery queryWithSex = new GVariantSearchQuery();
        GVariantSearchQueryParams paramsWithSex = new GVariantSearchQueryParams();
        paramsWithSex.setSex(GVariantSearchQueryParams.SexEnum.FEMALE);
        queryWithSex.setParams(paramsWithSex);
        assertEquals("_F", BeaconGVariantsRequestMapper.extractPopulationFilter(queryWithSex)
                .orElse(null));

        // Case 4: Only country set
        GVariantSearchQuery queryWithCountry = new GVariantSearchQuery();
        GVariantSearchQueryParams paramsWithCountry = new GVariantSearchQueryParams();
        paramsWithCountry.setCountryOfBirth(GVariantSearchQueryParams.CountryOfBirthEnum.FIN);
        queryWithCountry.setParams(paramsWithCountry);
        assertEquals("_FI", BeaconGVariantsRequestMapper.extractPopulationFilter(queryWithCountry)
                .orElse(null));

        // Case 5: Both sex and country set
        GVariantSearchQuery queryWithBoth = new GVariantSearchQuery();
        GVariantSearchQueryParams paramsWithBoth = new GVariantSearchQueryParams();
        paramsWithBoth.setSex(GVariantSearchQueryParams.SexEnum.MALE);
        paramsWithBoth.setCountryOfBirth(GVariantSearchQueryParams.CountryOfBirthEnum.FIN);
        queryWithBoth.setParams(paramsWithBoth);
        assertEquals("_M_FI", BeaconGVariantsRequestMapper.extractPopulationFilter(queryWithBoth)
                .orElse(null));
    }

    @Test
    @DisplayName("filterByPopulation should filter results by dataset name pattern")
    void filterByPopulation_WithPattern_FiltersResults() {
        List<GVariantsSearchResponse> results = List.of(
                GVariantsSearchResponse.builder().dataset("POPULATION_M_FI_dataset1").build(),
                GVariantsSearchResponse.builder().dataset("POPULATION_F_DE_dataset2").build()
        );

        List<GVariantsSearchResponse> filtered = BeaconGVariantsRequestMapper
                .filterByPopulation(results, Optional.of("M_FI"));

        assertEquals(1, filtered.size(), "Expected one dataset to match the population filter");
        assertEquals("POPULATION_M_FI_dataset1", filtered.get(0).getDataset(),
                "Filtered dataset name should match");
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

    @Test
    @DisplayName("iso3ToIso2 should fall back to uppercased ISO3 when mapping not found")
    void iso3ToIso2_WithUnmappedCountry_ReturnsUppercaseIso3() {
        String unmappedCountry = "XYZ";

        String result = BeaconGVariantsRequestMapper.iso3ToIso2(unmappedCountry);

        assertEquals("XYZ", result,
                "Unmapped country codes should return the uppercased ISO3 value");
    }

    @Test
    @DisplayName("map should handle BeaconResponse with empty results")
    void map_BeaconResponseWithEmptyResults_ReturnsEmptyList() {
        BeaconResponse response = new BeaconResponse();
        BeaconResponseContent content = new BeaconResponseContent();
        content.setResultSets(List.of());
        response.setResponse(content);

        List<GVariantsSearchResponse> result = BeaconGVariantsRequestMapper.map(response);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("map should handle result set with null results")
    void map_ResultSetWithNullResults_FiltersOut() {
        BeaconResponse response = new BeaconResponse();
        BeaconResponseContent content = new BeaconResponseContent();
        BeaconResultSet resultSet = new BeaconResultSet();
        resultSet.setResults(null);
        content.setResultSets(List.of(resultSet));
        response.setResponse(content);

        List<GVariantsSearchResponse> result = BeaconGVariantsRequestMapper.map(response);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("filterByPopulation should match datasets with population pattern")
    void filterByPopulation_WithMatchingPattern_ReturnsFilteredResults() {
        List<GVariantsSearchResponse> results = List.of(
                GVariantsSearchResponse.builder().dataset("COVID_M_FI").build(),
                GVariantsSearchResponse.builder().dataset("COVID_F_IT").build(),
                GVariantsSearchResponse.builder().dataset("COVID_M_DE").build()
        );

        List<GVariantsSearchResponse> filtered = BeaconGVariantsRequestMapper
                .filterByPopulation(results, Optional.of("M_FI"));

        assertEquals(1, filtered.size());
        assertEquals("COVID_M_FI", filtered.get(0).getDataset());
    }

    @Test
    @DisplayName("mapToAlleleFrequencyResponse should convert list to grouped allele frequency response")
    void mapToAlleleFrequencyResponse_WithVariants_ReturnsGroupedResponse() {
        List<GVariantsSearchResponse> results = List.of(
                GVariantsSearchResponse.builder()
                        .population(GVariantsSearchResponse.PopulationEnum.FINLAND)
                        .alleleFrequency(BigDecimal.valueOf(0.1234))
                        .alleleCount(BigDecimal.valueOf(100.0))
                        .alleleNumber(BigDecimal.valueOf(200.0))
                        .alleleCountHomozygous(BigDecimal.valueOf(5.0))
                        .alleleCountHeterozygous(BigDecimal.valueOf(95.0))
                        .dataset("dataset1")
                        .build(),
                GVariantsSearchResponse.builder()
                        .population(GVariantsSearchResponse.PopulationEnum.ITALY)
                        .alleleFrequency(BigDecimal.valueOf(0.5678))
                        .alleleCount(BigDecimal.valueOf(200.0))
                        .alleleNumber(BigDecimal.valueOf(400.0))
                        .alleleCountHomozygous(BigDecimal.valueOf(10.0))
                        .alleleCountHeterozygous(BigDecimal.valueOf(190.0))
                        .dataset("dataset2")
                        .build()
        );

        var response = BeaconGVariantsRequestMapper.mapToAlleleFrequencyResponse(results);

        assertNotNull(response, "Response should not be null");
        assertEquals(2, response.getNumberOfPopulations(), "Should have 2 populations");
        assertEquals("The Genome of Europe", response.getSource(),
                "Source should be 'The Genome of Europe'");
        assertEquals("https://genomeofeurope.eu/", response.getSourceReference(),
                "Source reference should be correct URL");
        assertEquals(2, response.getPopulations().size(), "Should have 2 population frequencies");

        // Check first population
        var firstPop = response.getPopulations().get(0);
        assertEquals("FIN", firstPop.getPopulation(), "Population should be FIN");
        assertEquals("0.1234", firstPop.getAlleleFrequency(), "Allele frequency should match");
        assertEquals("dataset1", firstPop.getDatasetId(), "Dataset ID should match");
        assertEquals("100.0", firstPop.getAlleleCount(), "Allele count should match");

        // Check second population
        var secondPop = response.getPopulations().get(1);
        assertEquals("ITA", secondPop.getPopulation(), "Population should be ITA");
        assertEquals("0.5678", secondPop.getAlleleFrequency(), "Allele frequency should match");
        assertEquals("dataset2", secondPop.getDatasetId(), "Dataset ID should match");
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
        freqInPop.setFrequencies(List.of(freq));

        var beaconResult = new Result();
        beaconResult.setFrequencyInPopulations(List.of(freqInPop));

        var resultSet = new BeaconResultSet();
        resultSet.setBeaconId("testBeaconId");
        resultSet.setId("testBeaconId");
        resultSet.setResults(List.of(beaconResult));

        var responseContent = new BeaconResponseContent();
        responseContent.setResultSets(List.of(resultSet));

        var responseData = new BeaconResponse();
        responseData.setResponse(responseContent);

        return responseData;
    }
}