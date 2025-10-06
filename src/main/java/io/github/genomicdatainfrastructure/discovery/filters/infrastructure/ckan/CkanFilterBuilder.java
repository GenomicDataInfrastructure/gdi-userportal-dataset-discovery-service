// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.ckan;

import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FilterBuilder;
import io.github.genomicdatainfrastructure.discovery.filters.infrastructure.quarkus.DatasetsConfig;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.model.FilterRange;
import io.github.genomicdatainfrastructure.discovery.model.FilterType;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFacet;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackageSearchRequest;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResult;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_FILTER_SOURCE;
import static java.util.Optional.ofNullable;

@ApplicationScoped
public class CkanFilterBuilder implements FilterBuilder {

    private static final String SELECTED_FACETS_PATTERN = "[\"%s\"]";

    private final CkanQueryApi ckanQueryApi;
    private final String selectedFacets;
    private final Map<String, FilterMetadata> filtersMetadata;

    public CkanFilterBuilder(
            @RestClient CkanQueryApi ckanQueryApi,
            DatasetsConfig datasetsConfig
    ) {
        this.ckanQueryApi = ckanQueryApi;
        this.selectedFacets = SELECTED_FACETS_PATTERN.formatted(String.join(
                "\",\"",
                datasetsConfig.filters().split(",")
        ));
        this.filtersMetadata = extractFiltersMetadata(datasetsConfig);
    }

    @Override
    public List<Filter> build(String accessToken, String preferredLanguage) {

        var request = PackageSearchRequest.builder()
                .rows(0)
                .start(0)
                .facetLimit(-1)
                .facetField(selectedFacets)
                .build();

        var response = ckanQueryApi.packageSearch(
                preferredLanguage,
                request
        );

        var nonNullSearchFacets = ofNullable(response.getResult())
                .map(PackagesSearchResult::getSearchFacets)
                .orElseGet(Map::of);

        return filters(nonNullSearchFacets);
    }

    private List<Filter> filters(Map<String, CkanFacet> facets) {
        var filtersByKey = new LinkedHashMap<String, Filter>();

        facets.entrySet()
                .stream()
                .map(this::filter)
                .filter(f -> f != null)
                .forEach(filter -> filtersByKey.put(filter.getKey(), filter));

        filtersMetadata.forEach((filterKey, metadata) -> {
            if (metadata.isDateTime) {
                filtersByKey.putIfAbsent(filterKey, buildDateTimeFilter(filterKey, null,
                        metadata.group));
            } else if (metadata.isNumber) {
                filtersByKey.putIfAbsent(filterKey, buildNumberFilter(filterKey, null,
                        metadata.group));
            }
        });

        return List.copyOf(filtersByKey.values());
    }

    private Filter filter(Map.Entry<String, CkanFacet> entry) {
        var key = entry.getKey();
        var facet = entry.getValue();

        var metadata = filtersMetadata.get(key);
        if (metadata != null && metadata.isDateTime) {
            return buildDateTimeFilter(key, facet, metadata.group);
        }

        if (metadata != null && metadata.isNumber) {
            return buildNumberFilter(key, facet, metadata.group);
        }

        var values = ofNullable(facet.getItems())
                .orElseGet(List::of)
                .stream()
                .map(value -> ValueLabel.builder()
                        .value(value.getName())
                        .label(value.getDisplayName())
                        .count(value.getCount())
                        .build())
                .toList();

        return Filter.builder()
                .source(CKAN_FILTER_SOURCE)
                .type(FilterType.DROPDOWN)
                .key(key)
                .label(facet.getTitle())
                .values(values)
                .group(metadata != null ? metadata.group : null)
                .build();
    }

    private Filter buildDateTimeFilter(String key, CkanFacet facet, String group) {
        var label = facet != null ? facet.getTitle() : null;
        return Filter.builder()
                .source(CKAN_FILTER_SOURCE)
                .type(FilterType.DATETIME)
                .key(key)
                .label(label)
                .range(extractDateTimeRange(facet))
                .group(group)
                .build();
    }

    private Filter buildNumberFilter(String key, CkanFacet facet, String group) {
        var label = facet != null ? facet.getTitle() : null;
        return Filter.builder()
                .source(CKAN_FILTER_SOURCE)
                .type(FilterType.NUMBER)
                .key(key)
                .label(label)
                .range(extractNumberRange(facet))
                .group(group)
                .build();
    }

    private FilterRange extractDateTimeRange(CkanFacet facet) {
        if (facet == null || facet.getItems() == null || facet.getItems().isEmpty()) {
            return null;
        }

        var bounds = facet.getItems().stream()
                .map(CkanValueLabel::getName)
                .map(this::parseOffsetDateTime)
                .flatMap(Optional::stream)
                .toList();

        if (bounds.isEmpty()) {
            return null;
        }

        var min = bounds.stream().min(Comparator.naturalOrder()).orElse(null);
        var max = bounds.stream().max(Comparator.naturalOrder()).orElse(null);

        if (min == null && max == null) {
            return null;
        }

        return FilterRange.builder()
                .min(min != null ? min.toString() : null)
                .max(max != null ? max.toString() : null)
                .build();
    }

    private FilterRange extractNumberRange(CkanFacet facet) {
        if (facet == null || facet.getItems() == null || facet.getItems().isEmpty()) {
            return null;
        }

        NumericBound min = null;
        NumericBound max = null;

        for (var item : facet.getItems()) {
            var bound = parseNumericValue(item.getName()).orElse(null);
            if (bound == null) {
                continue;
            }

            if (min == null || bound.numeric().compareTo(min.numeric()) < 0) {
                min = bound;
            }
            if (max == null || bound.numeric().compareTo(max.numeric()) > 0) {
                max = bound;
            }
        }

        var minValue = Optional.ofNullable(min)
                .map(NumericBound::raw)
                .orElse(null);
        var maxValue = Optional.ofNullable(max)
                .map(NumericBound::raw)
                .orElse(null);

        if (minValue == null && maxValue == null) {
            return null;
        }

        return FilterRange.builder()
                .min(minValue)
                .max(maxValue)
                .build();
    }

    private Optional<OffsetDateTime> parseOffsetDateTime(String value) {
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(OffsetDateTime.parse(value.trim()));
        } catch (DateTimeParseException exception) {
            return Optional.empty();
        }
    }

    private Optional<NumericBound> parseNumericValue(String value) {
        if (value == null) {
            return Optional.empty();
        }

        var trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(new NumericBound(new BigDecimal(trimmed), trimmed));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private Map<String, FilterMetadata> extractFiltersMetadata(DatasetsConfig datasetsConfig) {
        return ofNullable(datasetsConfig.filterGroups())
                .orElseGet(List::of)
                .stream()
                .flatMap(group -> ofNullable(group.filters()).orElseGet(Set::of)
                        .stream()
                        .map(filter -> Map.entry(filter.key(),
                                new FilterMetadata(Boolean.TRUE.equals(filter.isDateTime()),
                                        Boolean.TRUE.equals(filter.isNumber()), group.key()))))
                .collect(LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        Map::putAll);
    }

    private static final class FilterMetadata {

        private final boolean isDateTime;
        private final boolean isNumber;
        private final String group;

        private FilterMetadata(boolean isDateTime, boolean isNumber, String group) {
            this.isDateTime = isDateTime;
            this.isNumber = isNumber;
            this.group = group;
        }
    }

    private record NumericBound(BigDecimal numeric, String raw) {
    }
}
