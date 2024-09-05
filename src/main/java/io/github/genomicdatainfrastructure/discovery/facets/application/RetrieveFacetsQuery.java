// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.facets.application;

import io.github.genomicdatainfrastructure.discovery.facets.ports.IFacetBuilder;
import io.github.genomicdatainfrastructure.discovery.model.Facet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
@ApplicationScoped
public class RetrieveFacetsQuery {

    private final Instance<IFacetBuilder> facetsBuilders;

    public List<Facet> execute(String accessToken) {
        return facetsBuilders
                .stream()
                .map(facetBuilder -> facetBuilder.build(accessToken))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
