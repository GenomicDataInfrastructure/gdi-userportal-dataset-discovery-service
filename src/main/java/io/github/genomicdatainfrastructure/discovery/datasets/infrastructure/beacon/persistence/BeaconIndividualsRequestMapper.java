// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import static io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQuery.IncludeResultsetResponsesEnum.HIT;
import static io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQuery.RequestedGranularityEnum.RECORD;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

import io.github.genomicdatainfrastructure.discovery.datasets.domain.exceptions.InvalidFacetException;
import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequest;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestMeta;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQuery;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQueryFilter;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQueryFilter.OperatorEnum;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQueryPagination;
import io.smallrye.common.constraint.NotNull;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;

@UtilityClass
public class BeaconIndividualsRequestMapper {

    private static final String BEACON_FACET_GROUP = "beacon";
    private static final String SCOPE = "individual";

    public BeaconRequest from(
            DatasetSearchQuery query
    ) {
        var nonNullFacets = ofNullable(query)
                .map(DatasetSearchQuery::getFacets)
                .filter(ObjectUtils::isNotEmpty)
                .orElseGet(List::of);

        var beaconFilters = nonNullFacets.stream()
                .filter(it -> BEACON_FACET_GROUP.equals(it.getSource()))
                .filter(it -> FilterType.DROPDOWN.equals(it.getType()) || FilterType.FREE_TEXT
                        .equals(it.getType()))
                .map(BeaconIndividualsRequestMapper::buildBeaconFilter)
                .toList();

        var requestParameters = nonNullFacets.stream()
                .filter(it -> BEACON_FACET_GROUP.equals(it.getSource()))
                .filter(it -> FilterType.ENTRIES.equals(it.getType()))
                .map(BeaconIndividualsRequestMapper::buildBeaconRequestParameters)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                    Map.Entry::getKey, 
                    Map.Entry::getValue,
                    (existing, replacement) -> existing // Keep the first value in case of duplicates
                ));

        return BeaconRequest.builder()
                .meta(new BeaconRequestMeta())
                .query(BeaconRequestQuery.builder()
                        .includeResultsetResponses(HIT)
                        .requestedGranularity(RECORD)
                        .testMode(false)
                        .pagination(new BeaconRequestQueryPagination())
                        .filters(beaconFilters)
                        .requestParameters(requestParameters.isEmpty() ? null : requestParameters)
                        .build())
                .build();
    }

    private BeaconRequestQueryFilter buildBeaconFilter(DatasetSearchQueryFacet facet) {
        return switch (facet.getType()) {
            case DROPDOWN -> buildDropdownBeaconFacet(facet);
            case FREE_TEXT -> buildFreeTextBeaconFacet(facet);
            default -> throw new IllegalArgumentException("Invalid facet type");
        };
    }

    private BeaconRequestQueryFilter buildDropdownBeaconFacet(
            DatasetSearchQueryFacet facet) {
        String value = ofNullable(facet.getValue())
                .filter(not(String::isBlank))
                .orElseThrow(() -> new InvalidFacetException(
                        "Facet value must not be null or empty"));

        return BeaconRequestQueryFilter.builder()
                .id(value)
                .scope(SCOPE)
                .build();
    }

    private BeaconRequestQueryFilter buildFreeTextBeaconFacet(
            DatasetSearchQueryFacet facet) {
        String operator = ofNullable(facet.getOperator())
                .map(Operator::value)
                .orElseThrow(() -> new InvalidFacetException(
                        "Facet operator must not be null"));

        String value = ofNullable(facet.getValue())
                .filter(not(String::isBlank))
                .orElseThrow(() -> new InvalidFacetException(
                        "Facet value must not be null or empty"));

        String key = ofNullable(facet.getKey())
                .filter(not(String::isBlank))
                .orElseThrow(() -> new InvalidFacetException(
                        "Facet key must not be null or empty"));

        return BeaconRequestQueryFilter
                .builder()
                .id(key)
                .operator(OperatorEnum.fromString(operator))
                .value(value)
                .scope(SCOPE)
                .build();
    }

    private Map<String, String> buildBeaconRequestParameters(DatasetSearchQueryFacet facet) {
        return extractRequestParams(facet);
    }

    @NotNull
    static Map<String, String> extractRequestParams(DatasetSearchQueryFacet facet) {
        return ofNullable(facet.getEntries())
                .filter(not(List::isEmpty))
                .filter(entries -> entries
                        .stream()
                        .allMatch(e -> not(String::isBlank).test(e.getKey()) &&
                                not(String::isBlank).test(e.getValue()))
                )
                .orElseThrow(() -> new InvalidFacetException(
                        "Facet entries must not be empty or contain invalid key-value pairs"))
                .stream()
                .collect(toMap(QueryEntry::getKey, QueryEntry::getValue));
    }
}
