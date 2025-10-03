// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence.CkanFacetsQueryBuilder;
import io.github.genomicdatainfrastructure.discovery.model.FilterType;
import io.github.genomicdatainfrastructure.discovery.model.Operator;
import io.github.genomicdatainfrastructure.discovery.model.QueryOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;

class CkanFacetsQueryBuilderTest {

    @Test
    void canParse_whenEmptyList() {
        var query = new DatasetSearchQuery();
        query.setFacets(List.of());
        var expected = "";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(query);
        assertEquals(expected, actual);
    }

    @Test
    void canParse_whenNullList() {
        String expected = "";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(new DatasetSearchQuery());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void canParse_withAndOperator(String value) {
        var facets = List.of(
                DatasetSearchQueryFacet.builder()
                        .source("ckan")
                        .type(FilterType.DROPDOWN)
                        .key("field1")
                        .value("value1")
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source("ckan")
                        .type(FilterType.DROPDOWN)
                        .key("field1")
                        .value("value2")
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source("ckan")
                        .type(FilterType.DROPDOWN)
                        .key("field2")
                        .value("value3")
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source("dummy")
                        .type(FilterType.DROPDOWN)
                        .key("field2")
                        .value("value")
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source(null)
                        .type(FilterType.DROPDOWN)
                        .key("field2")
                        .value("value")
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source(null)
                        .key(null)
                        .value("value")
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source("ckan")
                        .type(FilterType.FREE_TEXT)
                        .key(value)
                        .value("value3")
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source("ckan")
                        .type(FilterType.DROPDOWN)
                        .key("field2")
                        .value(value)
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source(value)
                        .type(FilterType.DROPDOWN)
                        .key("field2")
                        .value("value3")
                        .build()
        );

        var query = new DatasetSearchQuery();
        query.setFacets(facets);
        query.setOperator(QueryOperator.AND);

        var expected = "field1:(\"value1\" AND \"value2\") AND field2:(\"value3\")";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(query);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "  "})
    void canParse_withOrOperator(String value) {
        var facets = List.of(
                DatasetSearchQueryFacet.builder()
                        .source("ckan")
                        .type(FilterType.DROPDOWN)
                        .key("field1")
                        .value("value1")
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source("ckan")
                        .type(FilterType.DROPDOWN)
                        .key("field1")
                        .value("value2")
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source("ckan")
                        .type(FilterType.DROPDOWN)
                        .key("field2")
                        .value("value3")
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source("dummy")
                        .type(FilterType.DROPDOWN)
                        .key("field2")
                        .value("value")
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source(null)
                        .type(FilterType.DROPDOWN)
                        .key("field2")
                        .value("value")
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source(null)
                        .key(null)
                        .value("value")
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source("ckan")
                        .type(FilterType.FREE_TEXT)
                        .key(value)
                        .value("value3")
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source("ckan")
                        .type(FilterType.DROPDOWN)
                        .key("field2")
                        .value(value)
                        .build(),
                DatasetSearchQueryFacet.builder()
                        .source(value)
                        .type(FilterType.DROPDOWN)
                        .key("field2")
                        .value("value3")
                        .build()
        );

        var query = new DatasetSearchQuery();
        query.setFacets(facets);
        query.setOperator(QueryOperator.OR);

        var expected = "field1:(\"value1\" OR \"value2\") AND field2:(\"value3\")";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(query);
        assertEquals(expected, actual);
    }

    @Test
    void canParse_withDateRangeFacet() {
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("ckan")
                                .type(FilterType.DATETIME)
                                .key("metadata_modified")
                                .operator(Operator.GREATER_THAN_OR_EQUAL_TO_SYMBOL)
                                .value("2024-01-01T00:00:00Z")
                                .build(),
                        DatasetSearchQueryFacet.builder()
                                .source("ckan")
                                .type(FilterType.DATETIME)
                                .key("metadata_modified")
                                .operator(Operator.LESS_THAN_OR_EQUAL_TO_SYMBOL)
                                .value("2024-12-31T23:59:59Z")
                                .build()
                ))
                .build();

        var expected = "metadata_modified:[\"2024-01-01T00:00:00Z\" TO \"2024-12-31T23:59:59Z\"]";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(query);

        assertEquals(expected, actual);
    }

    @Test
    void canParse_withDateRangeFacetAndDropdown() {
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("ckan")
                                .type(FilterType.DATETIME)
                                .key("metadata_modified")
                                .operator(Operator.GREATER_THAN_OR_EQUAL_TO_SYMBOL)
                                .value("2024-01-01T00:00:00Z")
                                .build(),
                        DatasetSearchQueryFacet.builder()
                                .source("ckan")
                                .type(FilterType.DROPDOWN)
                                .key("field1")
                                .value("value1")
                                .build()
                ))
                .build();

        var expected = "field1:(\"value1\") AND metadata_modified:[\"2024-01-01T00:00:00Z\" TO *]";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(query);

        assertEquals(expected, actual);
    }

    @Test
    void canParse_withConfigurableDateRangeFacetKey() {
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("ckan")
                                .type(FilterType.DATETIME)
                                .key("custom_datetime_field")
                                .operator(Operator.LESS_THAN_OR_EQUAL_TO_SYMBOL)
                                .value("2025-05-01T00:00:00Z")
                                .build()
                ))
                .build();

        var expected = "custom_datetime_field:[* TO \"2025-05-01T00:00:00Z\"]";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(query);

        assertEquals(expected, actual);
    }

    @Test
    void canParse_withNumberRangeFacet() {
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("ckan")
                                .type(FilterType.NUMBER)
                                .key("number_of_records")
                                .operator(Operator.GREATER_THAN_OR_EQUAL_TO_SYMBOL)
                                .value("10")
                                .build(),
                        DatasetSearchQueryFacet.builder()
                                .source("ckan")
                                .type(FilterType.NUMBER)
                                .key("number_of_records")
                                .operator(Operator.LESS_THAN_SYMBOL)
                                .value("20")
                                .build()
                ))
                .build();

        var expected = "number_of_records:[10 TO 20]";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(query);

        assertEquals(expected, actual);
    }

    @Test
    void canParse_withNumberRangeFacetAndDropdown() {
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("ckan")
                                .type(FilterType.NUMBER)
                                .key("number_of_records")
                                .operator(Operator.GREATER_THAN_SYMBOL)
                                .value("5")
                                .build(),
                        DatasetSearchQueryFacet.builder()
                                .source("ckan")
                                .type(FilterType.DROPDOWN)
                                .key("field1")
                                .value("value1")
                                .build()
                ))
                .build();

        var expected = "field1:(\"value1\") AND number_of_records:[5 TO *]";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(query);

        assertEquals(expected, actual);
    }

    @Test
    void canParse_withNumberExactFacet() {
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("ckan")
                                .type(FilterType.NUMBER)
                                .key("number_of_records")
                                .operator(Operator.EQUAL_SYMBOL)
                                .value("15")
                                .build()
                ))
                .build();

        var expected = "number_of_records:(15)";
        var actual = CkanFacetsQueryBuilder.buildFacetQuery(query);

        assertEquals(expected, actual);
    }
}
