// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.application.usecases;

import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FilterBuilder;
import io.github.genomicdatainfrastructure.discovery.filters.infrastructure.quarkus.DatasetsConfig;
import io.github.genomicdatainfrastructure.discovery.filters.infrastructure.quarkus.DatasetsConfig.FilterGroup;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RetrieveFiltersQuery {

    private final Instance<FilterBuilder> filterBuilders;
    private final DatasetsConfig datasetsConfig;

    public List<Filter> execute(String accessToken, String preferredLanguage) {
        return filterBuilders
                .stream()
                .map(filterBuilder -> filterBuilder.build(accessToken, preferredLanguage))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(this::mapGroup)
                .collect(Collectors.toList());
    }

    private Filter mapGroup(Filter filter) {
        var nonNullGroups = ofNullable(datasetsConfig.filterGroups())
                .orElseGet(List::of);

        var group = nonNullGroups.stream()
                .filter(it -> ofNullable(it.filters()).orElseGet(java.util.Set::of)
                        .stream()
                        .anyMatch(configuredFilter -> filter.getKey().equals(configuredFilter
                                .key())))
                .map(FilterGroup::key)
                .findFirst()
                .orElse(datasetsConfig.noGroupKey());

        filter.setGroup(group);

        return filter;
    }
}
