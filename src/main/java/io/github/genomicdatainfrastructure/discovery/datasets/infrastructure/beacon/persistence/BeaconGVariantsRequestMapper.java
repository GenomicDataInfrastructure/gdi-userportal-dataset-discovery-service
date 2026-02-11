// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.PARAM_REFERENCE_NAME;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.PARAM_START;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.PARAM_END;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.PARAM_REFERENCE_BASES;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.PARAM_ALTERNATE_BASES;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.PARAM_ASSEMBLY_ID;

import static io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconRequestQuery.IncludeResultsetResponsesEnum.HIT;
import static io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.model.BeaconRequestQuery.RequestedGranularityEnum.RECORD;
import static java.util.Optional.ofNullable;

public class BeaconGVariantsRequestMapper {

    public static BeaconRequest map(GVariantSearchQuery query) {
        var params = query.getParams();
        var requestParams = new HashMap<String, Object>();

        if (params != null) {
            addIfNonNull(requestParams, PARAM_REFERENCE_NAME, params.getReferenceName());
            addIfNonNull(requestParams, PARAM_START, params.getStart());
            addIfNonNull(requestParams, PARAM_END, params.getEnd());
            addIfNonNull(requestParams, PARAM_REFERENCE_BASES, params.getReferenceBases());
            addIfNonNull(requestParams, PARAM_ALTERNATE_BASES, params.getAlternateBases());
            addIfNonNull(requestParams, PARAM_ASSEMBLY_ID, params.getAssemblyId());
        }

        var beaconRequestQuery = new BeaconRequestQuery();
        beaconRequestQuery.setIncludeResultsetResponses(HIT);
        beaconRequestQuery.setRequestedGranularity(RECORD);
        beaconRequestQuery.setTestMode(false);
        beaconRequestQuery.setPagination(new BeaconRequestQueryPagination());
        beaconRequestQuery.setFilters(Collections.emptyList());
        beaconRequestQuery.setRequestParameters(requestParams.isEmpty() ? null : requestParams);

        var beaconRequest = new BeaconRequest();
        beaconRequest.setMeta(new BeaconRequestMeta());
        beaconRequest.setQuery(beaconRequestQuery);

        return beaconRequest;
    }

    private static void addIfNonNull(Map<String, Object> map, String key, Object value) {
        if (value instanceof Collection<?> a) {
            if (!a.isEmpty()) {
                map.put(key, value);
            }
        } else if (value != null) {
            map.put(key, value);
        }
    }

    public static List<GVariantsSearchResponse> map(BeaconResponse response) {
        var resultSets = ofNullable(response)
                .map(BeaconResponse::getResponse)
                .map(BeaconResponseContent::getResultSets)
                .orElse(Collections.emptyList());

        return resultSets.stream()
                .flatMap(resultSet -> mapResultSetToVariant(resultSet).stream())
                .toList();
    }

    private static List<GVariantsSearchResponse> mapResultSetToVariant(BeaconResultSet resultSet) {
        if (ObjectUtils.isEmpty(resultSet.getResults())) {
            return List.of();
        }

        return resultSet.getResults().stream()
                .filter(it -> ObjectUtils.isNotEmpty(it.getFrequencyInPopulations()))
                .flatMap(r -> r.getFrequencyInPopulations().stream())
                .filter(it -> ObjectUtils.isNotEmpty(it.getFrequencies()))
                .flatMap(fip -> fip.getFrequencies().stream())
                .map(freq -> populateVariantFromFrequency(freq, resultSet.getBeaconId(),
                        resultSet.getId())).toList();
    }

    private static GVariantsSearchResponse populateVariantFromFrequency(Frequency freq,
            String beaconId, String id) {
        GVariantsSearchResponse variant = new GVariantsSearchResponse();
        variant.beacon(beaconId);
        variant.datasetId(id);
        variant.alleleFrequency(freq.getAlleleFrequency());
        variant.population(freq.getPopulation());
        variant.alleleCount(freq.getAlleleCount());
        variant.alleleNumber(freq.getAlleleNumber());
        variant.alleleCountHomozygous(freq.getAlleleCountHomozygous());
        variant.alleleCountHeterozygous(freq.getAlleleCountHeterozygous());
        variant.alleleCountHemizygous(freq.getAlleleCountHemizygous());
        return variant;
    }
}
