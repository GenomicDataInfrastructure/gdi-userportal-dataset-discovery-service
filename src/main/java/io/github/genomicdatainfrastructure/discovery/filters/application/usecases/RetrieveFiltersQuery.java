// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.application.usecases;

import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FilterBuilder;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Log
public class RetrieveFiltersQuery {

    private final Instance<FilterBuilder> filterBuilders;

    public List<Filter> execute(String accessToken) {
        return filterBuilders
                .stream()
                .map(filterBuilder -> build(filterBuilder, accessToken))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Filter> build(FilterBuilder filterBuilder, String accessToken) {
        try {
            return filterBuilder.build(accessToken);
        } catch (Exception e) {
            log.log(Level.WARNING, "Failed to build filters", e);
            return null;
        }
    }
}
