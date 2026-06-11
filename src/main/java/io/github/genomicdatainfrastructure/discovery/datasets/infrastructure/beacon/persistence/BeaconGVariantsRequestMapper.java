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

    private static final int POSITION_RANGE_PAGE_LIMIT = 1000;

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
        beaconRequestQuery.setPagination(buildPagination(params));
        beaconRequestQuery.setFilters(Collections.emptyList());
        beaconRequestQuery.setRequestParameters(requestParams.isEmpty() ? null : requestParams);

        var beaconRequest = new BeaconRequest();
        beaconRequest.setMeta(new BeaconRequestMeta());
        beaconRequest.setQuery(beaconRequestQuery);

        return beaconRequest;
    }

    private static BeaconRequestQueryPagination buildPagination(GVariantSearchQueryParams params) {
        var pagination = new BeaconRequestQueryPagination();
        if (isPositionOnlyRangeQuery(params)) {
            // Position-only range queries should return overlapping variants, not just a single hit.
            pagination.setLimit(POSITION_RANGE_PAGE_LIMIT);
        }
        return pagination;
    }

    private static boolean isPositionOnlyRangeQuery(GVariantSearchQueryParams params) {
        return params != null
                && ObjectUtils.isNotEmpty(params.getEnd())
                && !PopulationConstants.hasValue(params.getReferenceBases())
                && !PopulationConstants.hasValue(params.getAlternateBases());
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
                .filter(result -> ObjectUtils.isNotEmpty(result.getFrequencyInPopulations()))
                .flatMap(result -> result.getFrequencyInPopulations().stream()
                        .filter(it -> ObjectUtils.isNotEmpty(it.getFrequencies()))
                        .flatMap(fip -> fip.getFrequencies().stream()
                                .map(freq -> populateVariantFromFrequency(freq, result,
                                        resultSet.getBeaconId(), resultSet.getId()))))
                .toList();
    }

    private static GVariantsSearchResponse populateVariantFromFrequency(Frequency freq,
            Result result,
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
        populateMatchedVariantDetails(variant, result);
        return variant;
    }

    private static void populateMatchedVariantDetails(GVariantsSearchResponse mapped,
            Result result) {
        var variation = result.getVariation();
        if (variation == null) {
            return;
        }

        mapped.referenceBases(variation.getReferenceBases());
        mapped.alternateBases(variation.getAlternateBases());

        var location = variation.getLocation();
        if (location == null) {
            return;
        }

        mapped.referenceName(extractReferenceName(location.getSequenceId()));

        var interval = location.getInterval();
        if (interval == null) {
            return;
        }

        mapped.start(ofNullable(interval.getStart())
                .map(GenomicVariationIntervalCoordinate::getValue)
                .orElse(null));
        mapped.end(ofNullable(interval.getEnd())
                .map(GenomicVariationIntervalCoordinate::getValue)
                .orElse(null));
    }

    private static String extractReferenceName(String sequenceId) {
        if (!PopulationConstants.hasValue(sequenceId)) {
            return null;
        }

        var normalized = sequenceId.trim();
        var tokens = normalized.split(":");
        if (tokens.length >= 2 && "HGVSID".equalsIgnoreCase(tokens[0])) {
            return normalizeReferenceName(tokens[1]);
        }

        return normalizeReferenceName(tokens.length == 1 ? tokens[0] : normalized);
    }

    private static String normalizeReferenceName(String referenceName) {
        if (!PopulationConstants.hasValue(referenceName)) {
            return null;
        }

        var trimmed = referenceName.trim();
        return trimmed.regionMatches(true, 0, "chr", 0, 3)
                ? trimmed.substring(3)
                : trimmed;
    }
}
