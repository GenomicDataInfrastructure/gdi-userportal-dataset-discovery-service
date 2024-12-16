// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence.CkanFacetsQueryBuilder;
import io.github.genomicdatainfrastructure.discovery.model.FilterType;
import io.github.genomicdatainfrastructure.discovery.model.QueryOperator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;

import javax.xml.crypto.Data;

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
}
