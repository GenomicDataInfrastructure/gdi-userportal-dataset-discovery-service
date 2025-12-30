// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQueryParams.CountryOfBirthEnum;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQueryParams.SexEnum;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.*;

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

    private static final Map<String, String> ISO3_TO_ISO2 = Map.ofEntries(
            Map.entry("aut", "AT"), Map.entry("bel", "BE"), Map.entry("bgr", "BG"),
            Map.entry("hrv", "HR"), Map.entry("cyp", "CY"), Map.entry("cze", "CZ"),
            Map.entry("dnk", "DK"), Map.entry("est", "EE"), Map.entry("fin", "FI"),
            Map.entry("fra", "FR"), Map.entry("deu", "DE"), Map.entry("grc", "GR"),
            Map.entry("hun", "HU"), Map.entry("irl", "IE"), Map.entry("ita", "IT"),
            Map.entry("lva", "LV"), Map.entry("ltu", "LT"), Map.entry("lux", "LU"),
            Map.entry("mlt", "MT"), Map.entry("nld", "NL"), Map.entry("pol", "PL"),
            Map.entry("prt", "PT"), Map.entry("rou", "RO"), Map.entry("svk", "SK"),
            Map.entry("svn", "SI"), Map.entry("esp", "ES"), Map.entry("swe", "SE"));

    private BeaconGVariantsRequestMapper() {
    }

    public static BeaconRequest map(GVariantSearchQuery query) {
        Map<String, Object> beaconParams = query.getParams().entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .filter(entry -> !LOCAL_FILTER_PARAMS.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        var requestQuery = new BeaconRequestQuery();
        requestQuery.setIncludeResultsetResponses(HIT);
        requestQuery.setRequestedGranularity(RECORD);
        requestQuery.setTestMode(false);
        requestQuery.setPagination(new BeaconRequestQueryPagination());
        requestQuery.setFilters(Collections.emptyList());
        requestQuery.setRequestParameters(beaconParams);

        var request = new BeaconRequest();
        request.setMeta(new BeaconRequestMeta());
        request.setQuery(requestQuery);

        return request;
    }

    public static Optional<String> extractPopulationFilter(GVariantSearchQuery query) {
        var params = ofNullable(query).map(GVariantSearchQuery::getParams);
        var sex = params.map(GVariantSearchQueryParams::getSex)
                .map(SexEnum::value)
                .map(s -> "male".equalsIgnoreCase(s) ? "M" : "F");
        var country = params.map(GVariantSearchQueryParams::getCountryOfBirth)
                .map(CountryOfBirthEnum::value)
                .map(iso3 -> ISO3_TO_ISO2.getOrDefault(iso3.toLowerCase(), iso3.toUpperCase()));

        if (sex.isEmpty() && country.isEmpty()) {
            return Optional.empty();
        }

        var pattern = new StringBuilder();
        sex.ifPresent(s -> pattern.append("_").append(s));
        country.ifPresent(c -> pattern.append("_").append(c));
        return Optional.of(pattern.toString());
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
        if (results == null || results.isEmpty() || populationFilter.isEmpty()) {
            return results;
        }
        var filter = populationFilter.get().toUpperCase();
        return results.stream()
                .filter(r -> r.getDataset() != null && r.getDataset().toUpperCase().contains(
                        filter))
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
        for (PopulationEnum population : PopulationEnum.values()) {
            if (input.contains(population.value())) {
                return population;
            }
        }
        return PopulationEnum.LUXEMBOURG;
    }
}
