// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.discovery.filters.application.usecases;

import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FiltersRepository;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RetrieveFiltersValuesQuery {

    private final FiltersRepository filtersRepository;

    public List<ValueLabel> execute(String key, String preferredLanguage) {
        return filtersRepository.getValuesForFilter(key, preferredLanguage);
    }

}
