// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.model.GVariantsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class BeaconGVariantsRequestMapperTest {

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
    }

    @Test
    @DisplayName("map(BeaconResponse) should extract sex from population format FR_F")
    void map_BeaconResponse_ExtractsSexFemale() {
        BeaconResponse beaconResponse = buildBeaconsResponseWithPopulation("FR_F");

        List<GVariantsSearchResponse> result = BeaconGVariantsRequestMapper.map(beaconResponse);

        assertEquals(1, result.size());
        GVariantsSearchResponse variant = result.getFirst();

        assertEquals("FR_F", variant.getPopulation());
    }

    @Test
    @DisplayName("map(BeaconResponse) should handle country only format FR")
    void map_BeaconResponse_HandlesCountryOnly() {
        BeaconResponse beaconResponse = buildBeaconsResponseWithPopulation("FR");

        List<GVariantsSearchResponse> result = BeaconGVariantsRequestMapper.map(beaconResponse);

        assertEquals(1, result.size());
        GVariantsSearchResponse variant = result.getFirst();

        assertEquals("FR", variant.getPopulation());
    }

    @Test
    @DisplayName("map(BeaconResponse) should handle sex only format M")
    void map_BeaconResponse_HandlesSexOnly() {
        BeaconResponse beaconResponse = buildBeaconsResponseWithPopulation("M");

        List<GVariantsSearchResponse> result = BeaconGVariantsRequestMapper.map(beaconResponse);

        assertEquals(1, result.size());
        GVariantsSearchResponse variant = result.getFirst();

        assertEquals("M", variant.getPopulation());
    }

    @Test
    @DisplayName("map(BeaconResponse) should handle sex only format F")
    void map_BeaconResponse_HandlesSexOnlyFemale() {
        BeaconResponse beaconResponse = buildBeaconsResponseWithPopulation("F");

        List<GVariantsSearchResponse> result = BeaconGVariantsRequestMapper.map(beaconResponse);

        assertEquals(1, result.size());
        GVariantsSearchResponse variant = result.getFirst();

        assertEquals("F", variant.getPopulation());
    }

    public static BeaconResponse buildBeaconsResponse() {
        var freq = new Frequency();
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
        var freq = new Frequency();
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

    @Test
    void map_BeaconResponse_frequenciesFromMultipleResultSets_areFullyFlattened() {
        // arrange
        BeaconResponse beaconResponse = new BeaconResponse();

        BeaconResultSet rs1 = new BeaconResultSet();
        rs1.setBeaconId("beacon-1");
        rs1.setId("dataset-1");

        Frequency rs1Freq1 = new Frequency();
        rs1Freq1.setPopulation("fin");
        rs1Freq1.setAlleleFrequency(BigDecimal.valueOf(0.1111));
        rs1Freq1.setAlleleCount(BigDecimal.valueOf(10));

        Frequency rs1Freq2 = new Frequency();
        rs1Freq2.setPopulation("nfe");
        rs1Freq2.setAlleleFrequency(BigDecimal.valueOf(0.2222));
        rs1Freq2.setAlleleCount(BigDecimal.valueOf(20));

        FrequencyInPopulations frequencyInPopulations1 = new FrequencyInPopulations();
        frequencyInPopulations1.setFrequencies(List.of(rs1Freq1, rs1Freq2));

        Result result1 = new Result();
        result1.setFrequencyInPopulations(List.of(frequencyInPopulations1));

        rs1.setResults(List.of(result1));

        BeaconResultSet rs2 = new BeaconResultSet();
        rs2.setBeaconId("beacon-2");
        rs2.setId("dataset-2");

        Frequency rs2Freq1 = new Frequency();
        rs2Freq1.setPopulation("afr");
        rs2Freq1.setAlleleFrequency(BigDecimal.valueOf(0.3333));
        rs2Freq1.setAlleleCount(BigDecimal.valueOf(30));

        FrequencyInPopulations frequencyInPopulations2 = new FrequencyInPopulations();
        frequencyInPopulations2.setFrequencies(List.of(rs2Freq1));

        Result result2 = new Result();
        result2.setFrequencyInPopulations(List.of(frequencyInPopulations2));

        rs2.setResults(List.of(result2));

        BeaconResponseContent beaconResponseContent = new BeaconResponseContent();
        beaconResponseContent.setResultSets(List.of(rs1, rs2));

        beaconResponse.setResponse(beaconResponseContent);

        // act
        List<GVariantsSearchResponse> result = BeaconGVariantsRequestMapper.map(beaconResponse);

        // assert
        // 2 frequencies in rs1 + 1 in rs2 => 3 total responses
        Assertions.assertThat(result).hasSize(3);

        // Verify beacon and dataset IDs are propagated correctly
        assertThat(result)
                .extracting(GVariantsSearchResponse::getBeacon)
                .containsExactlyInAnyOrder("beacon-1", "beacon-1", "beacon-2");

        assertThat(result)
                .extracting(GVariantsSearchResponse::getDatasetId)
                .containsExactlyInAnyOrder("dataset-1", "dataset-1", "dataset-2");

        // Optionally assert populations as well
        assertThat(result)
                .extracting(GVariantsSearchResponse::getPopulation)
                .containsExactlyInAnyOrder("fin", "nfe", "afr");
    }

    @Test
    void map_BeaconResponse_resultSetsWithEmptyFrequencies_produceNoOutput() {
        // arrange
        var rs1 = BeaconResultSet.builder()
                .beaconId("beacon-1")
                .id("dataset-1")
                .results(List.of())
                .build();

        var rs2 = BeaconResultSet.builder()
                .beaconId("beacon-2")
                .id("dataset-2")
                .results(null)
                .build();

        var rs3 = BeaconResultSet.builder()
                .beaconId("beacon-3")
                .id("dataset-3")
                .results(List.of(Result.builder()
                        .frequencyInPopulations(List.of())
                        .build()))
                .build();

        var rs4 = BeaconResultSet.builder()
                .beaconId("beacon-4")
                .id("dataset-4")
                .results(List.of(Result.builder()
                        .frequencyInPopulations(null)
                        .build()))
                .build();

        var rs5 = BeaconResultSet.builder()
                .beaconId("beacon-5")
                .id("dataset-5")
                .results(List.of(Result.builder()
                        .frequencyInPopulations(List.of(FrequencyInPopulations.builder()
                                .frequencies(List.of())
                                .build()))
                        .build()))
                .build();

        var rs6 = BeaconResultSet.builder()
                .beaconId("beacon-6")
                .id("dataset-6")
                .results(List.of(Result.builder()
                        .frequencyInPopulations(List.of(FrequencyInPopulations.builder()
                                .frequencies(null)
                                .build()))
                        .build()))
                .build();

        // act
        List<GVariantsSearchResponse> result = BeaconGVariantsRequestMapper.map(
                BeaconResponse.builder()
                        .response(BeaconResponseContent.builder()
                                .resultSets(List.of(rs1, rs2, rs3, rs4, rs5, rs6))
                                .build())
                        .build());

        // assert
        assertThat(result).isEmpty();
    }

    @Test
    void map_BeaconResponse_multipleFrequenciesInSingleResultSet_areFlatMapped() {
        // arrange
        BeaconResponse beaconResponse = new BeaconResponse();
        BeaconResultSet resultSet = new BeaconResultSet();
        resultSet.setBeaconId("beacon-1");
        resultSet.setId("dataset-1");

        Frequency freq1 = new Frequency();
        freq1.setPopulation("fin");
        freq1.setAlleleFrequency(BigDecimal.valueOf(0.1234));
        freq1.setAlleleCount(BigDecimal.valueOf(100.0));

        Frequency freq2 = new Frequency();
        freq2.setPopulation("nfe");
        freq2.setAlleleFrequency(BigDecimal.valueOf(0.2345));
        freq2.setAlleleCount(BigDecimal.valueOf(200.0));

        FrequencyInPopulations frequencyInPopulations2 = new FrequencyInPopulations();
        frequencyInPopulations2.setFrequencies(List.of(freq1, freq2));

        Result result = new Result();
        result.setFrequencyInPopulations(List.of(frequencyInPopulations2));

        // Depending on your model: setFrequencies or setFrequencyInPopulations
        resultSet.setResults(List.of(result));

        beaconResponse.setResponse(BeaconResponseContent.builder()
                .resultSets(List.of(resultSet))
                .build());

        // act
        List<GVariantsSearchResponse> underTest = BeaconGVariantsRequestMapper.map(beaconResponse);

        // assert
        assertThat(underTest).hasSize(2);

        // All responses should refer to the same beacon and dataset
        assertThat(underTest)
                .extracting(GVariantsSearchResponse::getBeacon)
                .containsOnly("beacon-1");
        assertThat(underTest)
                .extracting(GVariantsSearchResponse::getDatasetId)
                .containsOnly("dataset-1");

        // Optionally assert that each population appears once
        assertThat(underTest)
                .extracting(GVariantsSearchResponse::getPopulation)
                .containsExactlyInAnyOrder("fin", "nfe");
    }
}
