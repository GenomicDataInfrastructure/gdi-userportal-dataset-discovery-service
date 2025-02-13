// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.*;

import java.util.Collections;
import java.util.List;

import static io.github.genomicdatainfrastructure.discovery.model.GVariantsSearchResponse.*;
import static io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconRequestQuery.IncludeResultsetResponsesEnum.HIT;
import static io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconRequestQuery.RequestedGranularityEnum.RECORD;

public class BeaconGVariantsRequestMapper {

    public static BeaconRequest map(GVariantSearchQuery query) {
        return BeaconRequest.builder()
                .meta(new BeaconRequestMeta())
                .query(BeaconRequestQuery.builder()
                        .includeResultsetResponses(HIT)
                        .requestedGranularity(RECORD)
                        .testMode(false)
                        .pagination(new BeaconRequestQueryPagination())
                        .filters(Collections.emptyList())
                        .requestParameters(query.getParams())
                        .build())
                .build();

    }

    public static List<GVariantsSearchResponse> map(BeaconResponse response) {

        return response.getResponse().getResultSets().stream()
                .map(BeaconGVariantsRequestMapper::mapResultSetToVariant)
                .toList();
    }

    private static GVariantsSearchResponse mapResultSetToVariant(BeaconResultSet resultSet) {
        GVariantsSearchResponse variant = new GVariantsSearchResponse();
        variant.beacon(resultSet.getBeaconId());
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
