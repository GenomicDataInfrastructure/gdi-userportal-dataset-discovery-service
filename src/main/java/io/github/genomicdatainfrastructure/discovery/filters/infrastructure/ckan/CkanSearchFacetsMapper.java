// SPDX-FileCopyrightText: 2025 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.ckan;

import io.github.genomicdatainfrastructure.discovery.filters.infrastructure.quarkus.DatasetsConfig;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.model.FilterRange;
import io.github.genomicdatainfrastructure.discovery.model.FilterType;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFacet;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResult;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_FILTER_SOURCE;
import static java.util.Optional.ofNullable;

@ApplicationScoped
public class CkanSearchFacetsMapper {

    private static final String SELECTED_FACETS_PATTERN = "[\"%s\"]";
    private static final FilterType DEFAULT_FILTER_TYPE = FilterType.DROPDOWN;

    private final String selectedFacets;
    private final Map<String, FilterMetadata> filtersMetadata;

    public CkanSearchFacetsMapper(DatasetsConfig datasetsConfig) {
        this.selectedFacets = SELECTED_FACETS_PATTERN.formatted(String.join(
                "\",\"",
                Arrays.stream(datasetsConfig.filters().split(",")).map(String::trim).toList()
        ));
        this.filtersMetadata = extractFiltersMetadata(datasetsConfig);
    }

    public String selectedFacetField() {
        return selectedFacets;
    }

    public List<Filter> map(PackagesSearchResult result) {
        var nonNullSearchFacets = ofNullable(result)
                .map(PackagesSearchResult::getSearchFacets)
                .orElseGet(Map::of);

        return filters(nonNullSearchFacets);
    }

    private List<Filter> filters(Map<String, CkanFacet> facets) {
        var filtersByKey = new LinkedHashMap<String, Filter>();
        var processedKeys = new HashSet<String>();

        facets.entrySet()
                .stream()
                .filter(entry -> !processedKeys.contains(entry.getKey()))
                .map(f -> this.filter(f, facets, processedKeys))
                .filter(Objects::nonNull)
                .forEach(filter -> filtersByKey.put(filter.getKey(), filter));

        filtersMetadata.forEach((filterKey, metadata) -> {
            if (FilterType.DATETIME.equals(metadata.type)) {
                filtersByKey.putIfAbsent(filterKey, buildDateTimeFilter(filterKey, null,
                        metadata.group));
                return;
            }
            if (FilterType.NUMBER.equals(metadata.type)) {
                filtersByKey.putIfAbsent(filterKey, buildNumberFilter(filterKey, null,
                        metadata.group));
            }
        });

        return List.copyOf(filtersByKey.values());
    }

    private Filter filter(Map.Entry<String, CkanFacet> entry, Map<String, CkanFacet> facets,
            Set<String> skipList) {
        var key = entry.getKey();
        var facet = entry.getValue();

        var metadata = filtersMetadata.get(key);
        if (metadata == null) {
            // check if this is part of a range composite
            var rangeComponentMatch = filtersMetadata.entrySet()
                    .stream()
                    .filter(e -> e.getValue().rangeComposite.contains(entry.getKey()))
                    .findFirst();
            if (rangeComponentMatch.isPresent()) {
                var component = rangeComponentMatch.orElseThrow();
                key = component.getKey();
                metadata = component.getValue();
            }
        }

        if (metadata != null && !metadata.rangeComposite.isEmpty()) {
            // Processing a composite range facet. We'll gather individual components and combine them into the
            // composite facet.
            var items = new ArrayList<CkanValueLabel>();
            var titles = new LinkedHashSet<String>();

            for (var component : metadata.rangeComposite) {
                var componentFacet = facets.get(component);
                items.addAll(componentFacet.getItems());
                titles.add(componentFacet.getTitle());

                // skip processing the individual components
                skipList.add(component);
            }

            // replace the items (values) for the composite facet with the values collected from the individual facets
            facet.items(items);
            facet.setTitle(String.join(" - ", titles));
        }

        if (metadata != null && FilterType.DATETIME.equals(metadata.type)) {
            return buildDateTimeFilter(key, facet, metadata.group);
        }

        if (metadata != null && FilterType.NUMBER.equals(metadata.type)) {
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

        var type = Optional.ofNullable(metadata)
                .map(m -> m.type)
                .orElse(DEFAULT_FILTER_TYPE);

        return Filter.builder()
                .source(CKAN_FILTER_SOURCE)
                .type(type)
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

        var min = bounds.stream().min(Comparator.naturalOrder()).orElseThrow();
        var max = bounds.stream().max(Comparator.naturalOrder()).orElseThrow();

        return FilterRange.builder()
                .min(min.toString())
                .max(max.toString())
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
                                new FilterMetadata(Optional.ofNullable(filter.type())
                                        .orElse(DEFAULT_FILTER_TYPE),
                                        group.key(),
                                        filter.rangeComposite().orElse(List.of())))))
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (left, right) -> right, LinkedHashMap::new));
    }

    private record FilterMetadata(FilterType type, String group, List<String> rangeComposite) {
    }

    private record NumericBound(BigDecimal numeric, String raw) {
    }
}
