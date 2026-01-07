// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.genomicdatainfrastructure.discovery.model.GVariantsSearchResponse.*;
import static io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconRequestQuery.IncludeResultsetResponsesEnum.HIT;
import static io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconRequestQuery.RequestedGranularityEnum.RECORD;
import static java.util.Optional.ofNullable;

public class BeaconGVariantsRequestMapper {

    private static final Set<String> LOCAL_FILTER_PARAMS = Set.of("sex", "countryOfBirth");
    private static final String SOURCE_NAME = "The Genome of Europe";
    private static final String SOURCE_REFERENCE = "https://genomeofeurope.eu/";
    private static final String SEX_FILTER_ID = "sexFilter";
    private static final String COUNTRY_FILTER_ID = "countryFilter";
    private static final String POPULATION_SCOPE = "population";
    private static final String HEMIZYGOUS_COUNT = "0";

    private BeaconGVariantsRequestMapper() {
    }

    public static BeaconRequest map(GVariantSearchQuery query) {
        var params = ofNullable(query).map(GVariantSearchQuery::getParams).orElse(
                new GVariantSearchQueryParams());
        Map<String, Object> beaconParams = params.entrySet().stream()
                .filter(e -> e.getValue() != null && !LOCAL_FILTER_PARAMS.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        var filters = new java.util.ArrayList<BeaconRequestQueryFilter>();
        ofNullable(params.getSex()).ifPresent(s -> filters.add(BeaconRequestQueryFilter.builder()
                .id(SEX_FILTER_ID)
                .operator(BeaconRequestQueryFilter.OperatorEnum.EQUAL_SYMBOL)
                .value(s.value())
                .scope(POPULATION_SCOPE)
                .build()));
        ofNullable(params.getCountryOfBirth()).ifPresent(c -> filters.add(BeaconRequestQueryFilter
                .builder()
                .id(COUNTRY_FILTER_ID)
                .operator(BeaconRequestQueryFilter.OperatorEnum.EQUAL_SYMBOL)
                .value(c.value().toUpperCase())
                .scope(POPULATION_SCOPE)
                .build()));

        var requestQuery = new BeaconRequestQuery();
        requestQuery.setIncludeResultsetResponses(HIT);
        requestQuery.setRequestedGranularity(RECORD);
        requestQuery.setTestMode(false);
        requestQuery.setPagination(new BeaconRequestQueryPagination());
        requestQuery.setFilters(filters.isEmpty() ? Collections.emptyList() : filters);
        requestQuery.setRequestParameters(beaconParams);

        var request = new BeaconRequest();
        request.setMeta(new BeaconRequestMeta());
        request.setQuery(requestQuery);

        return request;
    }

    public static Optional<String> extractPopulationFilter(GVariantSearchQuery query) {
        var params = ofNullable(query).map(GVariantSearchQuery::getParams);
        var sexOpt = params.map(GVariantSearchQueryParams::getSex)
                .map(s -> switch (s) {
                    case MALE -> "M";
                    case FEMALE -> "F";
                    default -> null;
                });

        var countryOpt = params.map(GVariantSearchQueryParams::getCountryOfBirth)
                .map(GVariantSearchQueryParams.CountryOfBirthEnum::value)
                .map(String::toUpperCase);

        if (sexOpt.isEmpty() && countryOpt.isEmpty())
            return Optional.empty();

        var sb = new StringBuilder();
        sexOpt.ifPresent(s -> sb.append('_').append(s));
        countryOpt.ifPresent(c -> sb.append('_').append(c));
        return Optional.of(sb.toString());
    }

    public static List<GVariantsSearchResponse> map(BeaconResponse response) {
        return ofNullable(response)
                .map(BeaconResponse::getResponse)
                .map(BeaconResponseContent::getResultSets)
                .map(resultSets -> resultSets.stream()
                        .map(BeaconGVariantsRequestMapper::mapResultSetToVariant)
                        .filter(Objects::nonNull)
                        .toList())
                .orElse(Collections.emptyList());
    }

    public static List<GVariantsSearchResponse> filterByPopulation(
            List<GVariantsSearchResponse> results,
            Optional<String> populationFilter) {
        if (results == null || results.isEmpty() || populationFilter.isEmpty())
            return results;
        var filter = populationFilter.get().toUpperCase();
        var pattern = java.util.regex.Pattern.compile("(?i)(?:^|_)(" + java.util.regex.Pattern
                .quote(filter) + ")(?:_|$)");
        return results.stream()
                .filter(r -> r.getDataset() != null && pattern.matcher(r.getDataset()).find())
                .toList();
    }

    private static GVariantsSearchResponse mapResultSetToVariant(BeaconResultSet resultSet) {
        if (resultSet == null || resultSet.getResults() == null) {
            return null;
        }
        GVariantsSearchResponse variant = new GVariantsSearchResponse();
        variant.beacon(resultSet.getBeaconId());
        variant.dataset(resultSet.getId());
        resultSet.getResults().stream()
                .flatMap(r -> r.getFrequencyInPopulations().stream())
                .flatMap(fip -> fip.getFrequencies().stream())
                .forEach(freq -> populateVariantFromFrequency(variant, freq));
        return variant;
    }

    private static void populateVariantFromFrequency(GVariantsSearchResponse variant,
            Frequencies freq) {
        variant.alleleFrequency(freq.getAlleleFrequency());
        variant.population(mapPopulation(freq.getPopulation()));
        variant.alleleCount(freq.getAlleleCount());
        variant.alleleNumber(freq.getAlleleNumber());
        variant.alleleCountHomozygous(freq.getAlleleCountHomozygous());
        variant.alleleCountHeterozygous(freq.getAlleleCountHeterozygous());
    }

    private static PopulationEnum mapPopulation(String input) {
        if (input == null)
            return PopulationEnum.LUXEMBOURG;
        for (PopulationEnum population : PopulationEnum.values()) {
            if (input.contains(population.value()))
                return population;
        }
        return PopulationEnum.LUXEMBOURG;
    }

    public static GVariantAlleleFrequencyResponse mapToAlleleFrequencyResponse(
            List<GVariantsSearchResponse> results, GoEPopulationPatternExtractor extractor) {
        if (results == null || results.isEmpty()) {
            return new GVariantAlleleFrequencyResponse();
        }

        return GVariantAlleleFrequencyResponse.builder()
                .numberOfPopulations(results.size())
                .source(SOURCE_NAME)
                .sourceReference(SOURCE_REFERENCE)
                .populations(results.stream()
                        .map(r -> mapVariantToPopulationFrequency(r, extractor))
                        .collect(Collectors.toList()))
                .build();
    }

    private static PopulationFrequency mapVariantToPopulationFrequency(
            GVariantsSearchResponse variant, GoEPopulationPatternExtractor extractor) {
        String population = ofNullable(variant.getPopulation())
                .map(PopulationEnum::value)
                .map(String::toUpperCase)
                .or(() -> ofNullable(variant.getDataset())
                        .flatMap(dn -> ofNullable(extractor.extractPopulation(dn))))
                .orElse(null);

        return PopulationFrequency.builder()
                .population(population)
                .datasetId(variant.getDataset())
                .alleleFrequency(ofNullable(variant.getAlleleFrequency())
                        .map(BigDecimal::toPlainString)
                        .orElse(null))
                .alleleCount(ofNullable(variant.getAlleleCount())
                        .map(BigDecimal::toPlainString)
                        .orElse(null))
                .alleleNumber(ofNullable(variant.getAlleleNumber())
                        .map(BigDecimal::toPlainString)
                        .orElse(null))
                .alleleCountHomozygous(ofNullable(variant.getAlleleCountHomozygous())
                        .map(BigDecimal::toPlainString)
                        .orElse(null))
                .alleleCountHeterozygous(ofNullable(variant.getAlleleCountHeterozygous())
                        .map(BigDecimal::toPlainString)
                        .orElse(null))
                .alleleCountHemizygous(HEMIZYGOUS_COUNT)
                .build();
    }
}
