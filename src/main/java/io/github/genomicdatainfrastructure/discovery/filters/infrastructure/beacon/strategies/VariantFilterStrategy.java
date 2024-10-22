// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon.strategies;

import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.model.FilterEntry;
import io.github.genomicdatainfrastructure.discovery.model.FilterType;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconFilteringTermsResponseContent;

import java.util.List;

import static io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon.strategies.FilterStrategyContext.BEACON_FACET_GROUP;

public class VariantFilterStrategy implements BeaconFilterStrategy {

    @Override
    public List<Filter> buildFilters(BeaconFilteringTermsResponseContent content) {
        var variantEntries = buildVariantEntries();

        return variantEntries.stream().map(
                it -> Filter.builder()
                        .source(BEACON_FACET_GROUP)
                        .type(FilterType.ENTRIES)
                        .key("mutations")
                        .label("Mutations in:")
                        .entries(it)
                        .build()).toList();
    }

    private List<List<FilterEntry>> buildVariantEntries() {
        return List.of(
                List.of(
                        FilterEntry
                                .builder()
                                .key("geneId")
                                .label("gene")
                                .build(),
                        FilterEntry.builder()
                                .key("aminoacidChange")
                                .label("aminoacid")
                                .build()
                ),
                List.of(
                        FilterEntry
                                .builder()
                                .key("geneId")
                                .label("gene")
                                .build()
                ),
                List.of(
                        FilterEntry
                                .builder()
                                .key("alternateBases")
                                .label("alternateBases")
                                .build(),
                        FilterEntry.builder()
                                .key("referenceBases")
                                .label("referenceBases")
                                .build(),
                        FilterEntry.builder()
                                .key("start")
                                .label("start")
                                .build(),
                        FilterEntry.builder()
                                .key("end")
                                .label("end")
                                .build(),
                        FilterEntry.builder()
                                .key("variantType")
                                .label("type")
                                .build()
                ),
                List.of(
                        FilterEntry
                                .builder()
                                .key("alternateBases")
                                .label("alternateBases")
                                .build(),
                        FilterEntry.builder()
                                .key("referenceBases")
                                .label("referenceBases")
                                .build(),
                        FilterEntry.builder()
                                .key("start")
                                .label("start")
                                .build(),
                        FilterEntry.builder()
                                .key("end")
                                .label("end")
                                .build(),
                        FilterEntry.builder()
                                .key("type")
                                .label("type")
                                .build(),
                        FilterEntry.builder()
                                .key("assemblyId")
                                .label("assemblyId")
                                .build()
                ));
    }
}
