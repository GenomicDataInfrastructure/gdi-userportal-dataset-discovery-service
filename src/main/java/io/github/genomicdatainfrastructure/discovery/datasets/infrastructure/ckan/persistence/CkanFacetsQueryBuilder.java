// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils.CkanQueryOperatorMapper;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;
import io.github.genomicdatainfrastructure.discovery.model.FilterType;
import io.github.genomicdatainfrastructure.discovery.model.Operator;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

@UtilityClass
public class CkanFacetsQueryBuilder {

    private static final String CKAN_FACET_GROUP = "ckan";
    private static final String QUOTED_VALUE = "\"%s\"";
    private static final String FACET_PATTERN = "%s:(%s)";
    private static final String RANGE_PATTERN = "%s:[%s TO %s]";
    private static final String RANGE_WILDCARD = "*";
    private static final String AND = " AND ";

    public String buildFacetQuery(DatasetSearchQuery query) {
        var operator = CkanQueryOperatorMapper.getOperator(query.getOperator());

        var facetsByKey = ofNullable(query.getFacets())
                .orElseGet(List::of)
                .stream()
                .filter(CkanFacetsQueryBuilder::belongsToCkan)
                .collect(groupingBy(DatasetSearchQueryFacet::getKey));

        return facetsByKey.entrySet().stream()
                .map(entry -> getFacetQuery(entry.getKey(), entry.getValue(), operator))
                .flatMap(Optional::stream)
                .collect(joining(AND));
    }

    private Boolean belongsToCkan(DatasetSearchQueryFacet facet) {
        return Objects.equals(CKAN_FACET_GROUP, facet.getSource()) &&
                nonNull(facet.getKey()) &&
                !facet.getKey().isBlank();
    }

    private Optional<String> getFacetQuery(
            String key, List<DatasetSearchQueryFacet> facets, String operator
    ) {
        if (key == null || key.isBlank()) {
            return Optional.empty();
        }

        var rangeQuery = buildDateTimeRangeQuery(key, facets);
        if (rangeQuery.isPresent()) {
            return rangeQuery;
        }

        var values = facets.stream()
                .filter(facet -> !FilterType.DATETIME.equals(facet.getType()))
                .map(DatasetSearchQueryFacet::getValue)
                .filter(value -> nonNull(value) && !value.isBlank())
                .map(value -> QUOTED_VALUE.formatted(value))
                .collect(joining(operator));

        if (values.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(FACET_PATTERN.formatted(key, values));
    }

    private Optional<String> buildDateTimeRangeQuery(String key,
            List<DatasetSearchQueryFacet> facets) {
        return resolveDateTimeCondition(facets)
                .flatMap(condition -> condition.toSolrQuery(key));
    }

    private Optional<DateTimeCondition> resolveDateTimeCondition(
            List<DatasetSearchQueryFacet> facets) {
        var dateTimeFacets = facets.stream()
                .filter(facet -> FilterType.DATETIME.equals(facet.getType()))
                .toList();

        if (dateTimeFacets.isEmpty()) {
            return Optional.empty();
        }

        var lower = findValueByOperator(dateTimeFacets,
                Operator.GREATER_THAN_OR_EQUAL_TO_SYMBOL,
                Operator.GREATER_THAN_SYMBOL).orElse(null);

        var upper = findValueByOperator(dateTimeFacets,
                Operator.LESS_THAN_OR_EQUAL_TO_SYMBOL,
                Operator.LESS_THAN_SYMBOL).orElse(null);

        var exact = findValueByOperator(dateTimeFacets, Operator.EQUAL_SYMBOL).orElse(null);

        if (lower == null && upper == null && exact == null) {
            return Optional.empty();
        }

        return Optional.of(new DateTimeCondition(lower, upper, exact));
    }

    private Optional<String> findValueByOperator(
            List<DatasetSearchQueryFacet> facets, Operator... operators
    ) {
        if (operators == null || operators.length == 0) {
            return Optional.empty();
        }

        var allowedOperators = java.util.EnumSet.noneOf(Operator.class);
        allowedOperators.addAll(List.of(operators));

        return facets.stream()
                .filter(facet -> facet.getOperator() != null &&
                        allowedOperators.contains(facet.getOperator()))
                .map(DatasetSearchQueryFacet::getValue)
                .filter(value -> nonNull(value) && !value.isBlank())
                .map(String::trim)
                .findFirst();
    }

    private static String formatRangeBoundary(String value) {
        return value == null || value.isBlank()
                ? RANGE_WILDCARD
                : QUOTED_VALUE.formatted(value);
    }

    private record DateTimeCondition(String lowerBound, String upperBound, String exactMatch) {

        private boolean hasRange() {
            return lowerBound != null || upperBound != null;
        }

        private boolean hasExactMatch() {
            return exactMatch != null;
        }

        private Optional<String> toSolrQuery(String key) {
            if (hasRange()) {
                var start = formatRangeBoundary(lowerBound);
                var end = formatRangeBoundary(upperBound);
                return Optional.of(RANGE_PATTERN.formatted(key, start, end));
            }

            if (hasExactMatch()) {
                var value = QUOTED_VALUE.formatted(exactMatch);
                return Optional.of(FACET_PATTERN.formatted(key, value));
            }

            return Optional.empty();
        }
    }
}
