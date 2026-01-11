// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.POPULATION_PATTERN;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.hasValue;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.PARAM_VARIANT;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.PARAM_REF_GENOME;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.PARAM_COHORT;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.PARAM_SEX;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.PARAM_COUNTRY_OF_BIRTH;

import static io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconRequestQuery.IncludeResultsetResponsesEnum.HIT;
import static io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconRequestQuery.RequestedGranularityEnum.RECORD;
import static java.util.Optional.ofNullable;

public class BeaconGVariantsRequestMapper {

    public static BeaconRequest map(GVariantSearchQuery query) {
        var params = query.getParams();
        var requestParams = new HashMap<String, Object>();

        if (params != null) {
            addIfNonNull(requestParams, PARAM_VARIANT, params.getVariant());
            addIfNonNull(requestParams, PARAM_REF_GENOME, params.getRefGenome());
            addIfNonNull(requestParams, PARAM_COHORT, params.getCohort());
            addIfNonNull(requestParams, PARAM_SEX, params.getSex());
            addIfNonNull(requestParams, PARAM_COUNTRY_OF_BIRTH, params.getCountryOfBirth());
        }

        var beaconRequestQuery = new BeaconRequestQuery();
        beaconRequestQuery.setIncludeResultsetResponses(HIT);
        beaconRequestQuery.setRequestedGranularity(RECORD);
        beaconRequestQuery.setTestMode(false);
        beaconRequestQuery.setPagination(new BeaconRequestQueryPagination());
        beaconRequestQuery.setFilters(Collections.emptyList());
        beaconRequestQuery.setRequestParameters(requestParams);

        var beaconRequest = new BeaconRequest();
        beaconRequest.setMeta(new BeaconRequestMeta());
        beaconRequest.setQuery(beaconRequestQuery);

        return beaconRequest;
    }

    private static void addIfNonNull(HashMap<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
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
        variant.population(freq.getPopulation());
        variant.alleleCount(freq.getAlleleCount());
        variant.alleleNumber(freq.getAlleleNumber());
        variant.alleleCountHomozygous(freq.getAlleleCountHomozygous());
        variant.alleleCountHeterozygous(freq.getAlleleCountHeterozygous());

        // Extract sex and countryOfBirth from population string in GoE format [COUNTRY]_[SEX]
        extractAndSetSexAndCountry(variant, freq.getPopulation());
    }

    private static void extractAndSetSexAndCountry(GVariantsSearchResponse variant,
            String populationStr) {
        if (populationStr == null || populationStr.isBlank()) {
            return;
        }

        var matcher = POPULATION_PATTERN.matcher(populationStr);
        if (!matcher.matches()) {
            return;
        }

        String countryCode = matcher.group(1);
        String sexAfterCountry = matcher.group(2);
        String sexOnly = matcher.group(3);

        if (hasValue(countryCode)) {
            variant.setCountryOfBirth(countryCode.toUpperCase());
        }
        if (hasValue(sexAfterCountry)) {
            variant.setSex(sexAfterCountry.toUpperCase());
        } else if (hasValue(sexOnly)) {
            variant.setSex(sexOnly.toUpperCase());
        }
    }

}
