// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon.strategies;

import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconFilteringTermsResponseContent;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class FilterStrategyContext {

    protected static final String SUPPORTED_FILTER_SCOPE = "individual";
    protected static final String BEACON_FACET_GROUP = "beacon";

    private static final List<BeaconFilterStrategy> SUPPORTED_STRATEGIES = List.of(
            new OntologyFilterStrategy(),
            new AlphanumericFilterStrategy(),
            new VariantFilterStrategy()
    );

    public List<Filter> buildFilters(BeaconFilteringTermsResponseContent content) {
        return SUPPORTED_STRATEGIES
                .stream()
                .map(strategy -> strategy.buildFilters(content))
                .flatMap(List::stream)
                .toList();
    }
}
