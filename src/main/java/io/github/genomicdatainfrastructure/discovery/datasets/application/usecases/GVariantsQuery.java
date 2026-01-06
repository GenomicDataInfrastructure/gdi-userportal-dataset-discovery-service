// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.application.usecases;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.GVariantsRepositoryPort;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.GVariantAlleleFrequencyResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class GVariantsQuery {

    private final GVariantsRepositoryPort repository;

    public GVariantAlleleFrequencyResponse execute(GVariantSearchQuery gVariantSearchQuery) {
        return repository.search(gVariantSearchQuery);
    }
}
