// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQueryParams;
import io.github.genomicdatainfrastructure.discovery.model.GVariantsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.api.GVariantsApi;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconResponseContent;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconResultSet;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.Frequency;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.FrequencyInPopulations;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        var query = createQuery("21:9411448:G:T", "GRCh37", null, null);
        when(gVariantsApi.postGenomicVariationsRequest(any())).thenReturn(buildBeaconsResponse());

        var result = gVariantsRepository.search(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testBeaconId", result.getFirst().getBeacon());
    }

    @Test
    void givenCombinedFilters_whenSearch_thenFiltersExactMatch() {
        var query = createQuery("21:9411448:G:T", "GRCh37", "FR", "M");
        when(gVariantsApi.postGenomicVariationsRequest(any())).thenReturn(
                buildBeaconsResponseWithPopulations("FR_Male", "FR", "Male", "FR_Female"));

        var result = gVariantsRepository.search(query);

        assertEquals(1, result.size());
        assertEquals("FR_Male", result.getFirst().getPopulation());
    }

    @Test
    void givenSexOnlyFilter_whenSearch_thenFiltersToSexPopulationOnly() {
        var query = createQuery("21:9411448:G:T", "GRCh37", null, "M");
        when(gVariantsApi.postGenomicVariationsRequest(any())).thenReturn(
                buildBeaconsResponseWithPopulations("M", "Male", "FR_Male", "Female", "FR"));

        var result = gVariantsRepository.search(query);

        assertEquals(2, result.size());
        assertEquals(Set.of("M", "Male"), populationsOf(result));
    }

    @Test
    void givenCountryOnlyFilter_whenSearch_thenFiltersToCountryPopulationOnly() {
        var query = createQuery("21:9411448:G:T", "GRCh37", "FR", null);
        when(gVariantsApi.postGenomicVariationsRequest(any())).thenReturn(
                buildBeaconsResponseWithPopulations("FR", "FR_M", "M", "ES"));

        var result = gVariantsRepository.search(query);

        assertEquals(1, result.size());
        assertEquals("FR", result.getFirst().getPopulation());
    }

    @Test
    void givenCountryAllAndSexNotSelected_whenSearch_thenReturnsOnlyCountryOnlyPopulations() {
        var query = createQuery("21:9411448:G:T", "GRCh37", "ALL", null);
        when(gVariantsApi.postGenomicVariationsRequest(any())).thenReturn(
                buildBeaconsResponseWithPopulations("FR", "ES", "Male", "FR_Male"));

        var result = gVariantsRepository.search(query);

        assertEquals(Set.of("FR", "ES"), populationsOf(result));
    }

    @Test
    void givenSexAllAndCountryNotSelected_whenSearch_thenReturnsOnlySexOnlyPopulations() {
        var query = createQuery("21:9411448:G:T", "GRCh37", null, "ALL");
        when(gVariantsApi.postGenomicVariationsRequest(any())).thenReturn(
                buildBeaconsResponseWithPopulations("Male", "Female", "FR", "FR_Male"));

        var result = gVariantsRepository.search(query);

        assertEquals(Set.of("Male", "Female"), populationsOf(result));
    }

    @Test
    void givenCountryAllAndSexSpecific_whenSearch_thenReturnsCountrySexPopulationsAcrossCountries() {
        var query = createQuery("21:9411448:G:T", "GRCh37", "ALL", "M");
        when(gVariantsApi.postGenomicVariationsRequest(any())).thenReturn(
                buildBeaconsResponseWithPopulations("FR_Male", "ES_M", "Male", "FR"));

        var result = gVariantsRepository.search(query);

        assertEquals(Set.of("FR_Male", "ES_M"), populationsOf(result));
    }

    @Test
    void givenCountrySpecificAndSexAll_whenSearch_thenReturnsCountrySexPopulationsForAllSexes() {
        var query = createQuery("21:9411448:G:T", "GRCh37", "FR", "ALL");
        when(gVariantsApi.postGenomicVariationsRequest(any())).thenReturn(
                buildBeaconsResponseWithPopulations("FR_Male", "FR_Female", "FR", "ES_Male"));

        var result = gVariantsRepository.search(query);

        assertEquals(Set.of("FR_Male", "FR_Female"), populationsOf(result));
    }

    @Test
    void givenCountryAllAndSexAll_whenSearch_thenReturnsEverything() {
        var query = createQuery("21:9411448:G:T", "GRCh37", "ALL", "ALL");
        when(gVariantsApi.postGenomicVariationsRequest(any())).thenReturn(
                buildBeaconsResponseWithPopulations("FR", "Male", "FR_Male"));

        var result = gVariantsRepository.search(query);

        assertEquals(Set.of("FR", "Male", "FR_Male"), populationsOf(result));
    }

    @Test
    void acceptsNullParams() {
        var query = new GVariantSearchQuery();
        query.setParams(null);

        var result = gVariantsRepository.search(query);

        assertTrue(result.isEmpty());
        verify(gVariantsApi, times(1)).postGenomicVariationsRequest(any());
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

    private BeaconResponse buildBeaconsResponseWithPopulations(String... populations) {
        var frequencies = Arrays.stream(populations)
                .map(population -> {
                    var frequency = new Frequency();
                    frequency.setPopulation(population);
                    frequency.setAlleleFrequency(BigDecimal.valueOf(0.1234));
                    return frequency;
                })
                .toList();

        var frequenciesInPopulation = new FrequencyInPopulations();
        frequenciesInPopulation.setFrequencies(frequencies);

        var result = new Result();
        result.setFrequencyInPopulations(List.of(frequenciesInPopulation));

        var resultSet = new BeaconResultSet();
        resultSet.setBeaconId("testBeaconId");
        resultSet.setResults(List.of(result));

        var responseContent = new BeaconResponseContent();
        responseContent.setResultSets(List.of(resultSet));

        var response = new BeaconResponse();
        response.setResponse(responseContent);
        return response;
    }

    private Set<String> populationsOf(List<GVariantsSearchResponse> result) {
        return result.stream().map(it -> it.getPopulation()).collect(Collectors.toSet());
    }
}
