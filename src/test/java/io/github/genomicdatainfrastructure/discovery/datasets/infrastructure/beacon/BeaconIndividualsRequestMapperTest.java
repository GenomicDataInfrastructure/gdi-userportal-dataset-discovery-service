// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon;

import io.github.genomicdatainfrastructure.discovery.datasets.domain.exceptions.InvalidFacetException;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.BeaconIndividualsRequestMapper;
import io.github.genomicdatainfrastructure.discovery.model.*;
import org.junit.jupiter.api.Test;

import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequest;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestMeta;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQuery;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQueryFilter;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQueryPagination;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQuery.IncludeResultsetResponsesEnum.HIT;
import static io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQuery.RequestedGranularityEnum.RECORD;
import static io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQueryFilter.OperatorEnum.GREATER_THAN_SYMBOL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class BeaconRequestMapperTest {

    @Test
    void shouldAcceptNullQuery() {
        var actual = BeaconIndividualsRequestMapper.from(null);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(BeaconRequest.builder()
                        .meta(BeaconRequestMeta.builder()
                                .apiVersion("2.0")
                                .build())
                        .query(BeaconRequestQuery.builder()
                                .includeResultsetResponses(HIT)
                                .requestedGranularity(RECORD)
                                .testMode(false)
                                .pagination(BeaconRequestQueryPagination.builder()
                                        .limit(1)
                                        .skip(0)
                                        .build())
                                .filters(List.of())
                                .requestParameters(null)
                                .build())
                        .build());
    }

    @Test
    void shouldAcceptEmptyQuery() {
        var actual = BeaconIndividualsRequestMapper.from(DatasetSearchQuery.builder()
                .facets(null)
                .build());

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(BeaconRequest.builder()
                        .meta(BeaconRequestMeta.builder()
                                .apiVersion("2.0")
                                .build())
                        .query(BeaconRequestQuery.builder()
                                .includeResultsetResponses(HIT)
                                .requestedGranularity(RECORD)
                                .testMode(false)
                                .pagination(BeaconRequestQueryPagination.builder()
                                        .limit(1)
                                        .skip(0)
                                        .build())
                                .filters(List.of())
                                .requestParameters(null)
                                .build())
                        .build());
    }

    @Test
    void shouldAcceptEmptyFacets() {
        var actual = BeaconIndividualsRequestMapper.from(DatasetSearchQuery.builder()
                .facets(List.of())
                .build());

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(BeaconRequest.builder()
                        .meta(BeaconRequestMeta.builder()
                                .apiVersion("2.0")
                                .build())
                        .query(BeaconRequestQuery.builder()
                                .includeResultsetResponses(HIT)
                                .requestedGranularity(RECORD)
                                .testMode(false)
                                .pagination(BeaconRequestQueryPagination.builder()
                                        .limit(1)
                                        .skip(0)
                                        .build())
                                .filters(List.of())
                                .requestParameters(null)
                                .build())
                        .build());
    }

    @Test
    void canParse() {
        var actual = BeaconIndividualsRequestMapper.from(DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("beacon")
                                .type(FilterType.DROPDOWN)
                                .key("dummy_key_1")
                                .value("dummy_value_1")
                                .entries(List.of())
                                .build(),
                        DatasetSearchQueryFacet.builder()
                                .source("beacon")
                                .type(FilterType.FREE_TEXT)
                                .key("dummy_key_2")
                                .value("dummy_value_2")
                                .operator(Operator.GREATER_THAN_SYMBOL)
                                .entries(List.of())
                                .build(),
                        DatasetSearchQueryFacet.builder()
                                .source("beacon")
                                .type(FilterType.ENTRIES)
                                .key("dummy_key_3")
                                .entries(
                                        List.of(new QueryEntry("geneId", "KRAS"),
                                                new QueryEntry("aminoacidChange", "p.R121H"))
                                )
                                .build()))
                .build());

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(BeaconRequest.builder()
                        .meta(BeaconRequestMeta.builder()
                                .apiVersion("2.0")
                                .build())
                        .query(BeaconRequestQuery.builder()
                                .includeResultsetResponses(HIT)
                                .requestedGranularity(RECORD)
                                .testMode(false)
                                .pagination(BeaconRequestQueryPagination.builder()
                                        .limit(1)
                                        .skip(0)
                                        .build())
                                .filters(List.of(
                                        BeaconRequestQueryFilter.builder()
                                                .id("dummy_value_1")
                                                .scope("individual")
                                                .build(),
                                        BeaconRequestQueryFilter.builder()
                                                .id("dummy_key_2")
                                                .operator(GREATER_THAN_SYMBOL)
                                                .value("dummy_value_2")
                                                .scope("individual")
                                                .build()
                                ))
                                .requestParameters(List.of(
                                        Map.of("geneId", "KRAS",
                                                "aminoacidChange", "p.R121H")
                                ))
                                .build())
                        .build());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void shouldThrowBadRequest_WhenValueIsMissingOrEmpty_ForDropdownFilter(String value) {
        assertThatThrownBy(() -> BeaconIndividualsRequestMapper.from(DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("beacon")
                                .type(FilterType.DROPDOWN)
                                .key("dummy_key_1")
                                .value(value)
                                .build()))
                .build()))
                .isInstanceOf(InvalidFacetException.class)
                .hasMessage("Facet value must not be null or empty");
    }

    @Test
    void shouldThrowBadRequest_WhenOperatorIsMissing_ForFreeTextFilter() {
        assertThatThrownBy(() -> BeaconIndividualsRequestMapper.from(DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("beacon")
                                .type(FilterType.FREE_TEXT)
                                .key("dummy_key_1")
                                .value("value")
                                .build()))
                .build()))
                .isInstanceOf(InvalidFacetException.class)
                .hasMessage("Facet operator must not be null");
    }

    @Test
    void shouldThrowBadRequest_WhenEntriesAreMissing_ForEntriesFilter() {
        assertThatThrownBy(() -> BeaconIndividualsRequestMapper.from(DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("beacon")
                                .type(FilterType.ENTRIES)
                                .key("dummy_key_1")
                                .value("value")
                                .build()))
                .build()))
                .isInstanceOf(InvalidFacetException.class)
                .hasMessage("Facet entries must not be empty or contain invalid key-value pairs");
    }
}
