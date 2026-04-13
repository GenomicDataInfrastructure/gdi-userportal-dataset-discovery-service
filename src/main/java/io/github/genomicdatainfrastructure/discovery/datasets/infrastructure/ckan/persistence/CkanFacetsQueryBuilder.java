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
    private static final String TYPICAL_AGE_KEY = "typical_age";
    private static final String MIN_TYPICAL_AGE = "min_typical_age";
    private static final String MAX_TYPICAL_AGE = "max_typical_age";

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

        var rangeQuery = buildRangeQuery(key, facets);
        if (rangeQuery.isPresent()) {
            return rangeQuery;
        }

        var values = facets.stream()
                .filter(facet -> !FilterType.DATETIME.equals(facet.getType()) &&
                        !FilterType.NUMBER.equals(facet.getType()))
                .map(DatasetSearchQueryFacet::getValue)
                .filter(value -> nonNull(value) && !value.isBlank())
                .map(QUOTED_VALUE::formatted)
                .collect(joining(operator));

        if (values.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(FACET_PATTERN.formatted(key, values));
    }

    private Optional<String> buildRangeQuery(String key, List<DatasetSearchQueryFacet> facets) {
        return buildDateTimeRangeQuery(key, facets)
                .or(() -> buildNumberRangeQuery(key, facets));
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

    private Optional<String> buildNumberRangeQuery(String key,
            List<DatasetSearchQueryFacet> facets) {
        if (TYPICAL_AGE_KEY.equals(key)) {
            return buildTypicalAgeQuery(facets);
        }

        return resolveNumberCondition(facets)
                .flatMap(condition -> condition.toSolrQuery(key));
    }

    private Optional<String> buildTypicalAgeQuery(List<DatasetSearchQueryFacet> facets) {
        var numericFacets = facets.stream()
                .filter(facet -> FilterType.NUMBER.equals(facet.getType()))
                .toList();
        var lower = findValueByOperator(numericFacets, Operator.GREATER_THAN_SYMBOL,
                Operator.GREATER_THAN_OR_EQUAL_TO_SYMBOL);
        var upper = findValueByOperator(numericFacets, Operator.LESS_THAN_OR_EQUAL_TO_SYMBOL,
                Operator.LESS_THAN_SYMBOL);

        if (lower.isPresent() && upper.isEmpty()) {
            return resolveNumberCondition(numericFacets)
                    .flatMap(condition -> condition.toSolrQuery(MIN_TYPICAL_AGE));
        } else if (upper.isPresent() && lower.isEmpty()) {
            return resolveNumberCondition(numericFacets)
                    .flatMap(condition -> condition.toSolrQuery(MAX_TYPICAL_AGE));
        }

        var sb = new StringBuilder();

        // The mapping of lower/upper search bounds to max/min_typical_age respectively is intentional,
        // and is required to correctly identify datasets whose typical age range overlaps with the search range.
        //
        // A naive approach would map the search lower bound to min_typical_age and the search upper bound to
        // max_typical_age — but this would only match datasets whose age range is fully contained within the
        // search range, missing datasets that only partially overlap.
        //
        // Instead, we use the overlap condition: two ranges [A, B] and [C, D] overlap if and only if A <= D && C <= B.
        // Applied here: the search range is [lower, upper] and the dataset range is [min_typical_age, max_typical_age].
        // Overlap occurs when:
        //   - lower <= max_typical_age  (the search range starts before the dataset range ends)
        //   - min_typical_age <= upper  (the dataset range starts before the search range ends)
        upper.flatMap(value -> new NumberCondition(null, value, null).toSolrQuery(
                MIN_TYPICAL_AGE))
                .ifPresent(sb::append);
        lower.flatMap(value -> new NumberCondition(value, null, null).toSolrQuery(
                MAX_TYPICAL_AGE))
                .ifPresent(value -> {
                    if (!sb.isEmpty()) {
                        sb.append(AND);
                    }
                    sb.append(value);
                });

        if (sb.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(sb.toString());
    }

    private Optional<NumberCondition> resolveNumberCondition(List<DatasetSearchQueryFacet> facets) {
        var numericFacets = facets.stream()
                .filter(facet -> FilterType.NUMBER.equals(facet.getType()))
                .toList();

        if (numericFacets.isEmpty()) {
            return Optional.empty();
        }

        var lower = findValueByOperator(numericFacets,
                Operator.GREATER_THAN_OR_EQUAL_TO_SYMBOL,
                Operator.GREATER_THAN_SYMBOL).orElse(null);

        var upper = findValueByOperator(numericFacets,
                Operator.LESS_THAN_OR_EQUAL_TO_SYMBOL,
                Operator.LESS_THAN_SYMBOL).orElse(null);

        var exact = findValueByOperator(numericFacets, Operator.EQUAL_SYMBOL).orElse(null);

        if (lower == null && upper == null && exact == null) {
            return Optional.empty();
        }

        return Optional.of(new NumberCondition(lower, upper, exact));
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

    private static String formatNumberBoundary(String value) {
        return value == null || value.isBlank()
                ? RANGE_WILDCARD
                : value;
    }

    private static final class NumberCondition {

        private final String lowerBound;
        private final String upperBound;
        private final String exactMatch;

        private NumberCondition(String lowerBound, String upperBound, String exactMatch) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.exactMatch = exactMatch;
        }

        private boolean hasRange() {
            return lowerBound != null || upperBound != null;
        }

        private boolean hasExactMatch() {
            return exactMatch != null;
        }

        private Optional<String> toSolrQuery(String key) {
            if (hasRange()) {
                var start = formatNumberBoundary(lowerBound);
                var end = formatNumberBoundary(upperBound);
                return Optional.of(RANGE_PATTERN.formatted(key, start, end));
            }

            if (hasExactMatch()) {
                return Optional.of(FACET_PATTERN.formatted(key, exactMatch));
            }

            return Optional.empty();
        }
    }
}
