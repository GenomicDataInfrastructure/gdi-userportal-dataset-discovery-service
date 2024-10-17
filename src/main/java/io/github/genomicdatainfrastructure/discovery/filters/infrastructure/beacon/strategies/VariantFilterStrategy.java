package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon.strategies;

import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.model.FilterInputEntry;
import io.github.genomicdatainfrastructure.discovery.model.FilterInputs;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconFilteringTermsResponseContent;

import java.util.List;

import static io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon.strategies.FilterStrategyContext.BEACON_FACET_GROUP;

public class VariantFilterStrategy implements BeaconFilterStrategy {

    @Override
    public List<Filter> buildFilters(BeaconFilteringTermsResponseContent content) {
        var filterInputEntries = buildEntries();

        return filterInputEntries.stream().map(
                it -> Filter.builder()
                        .source(BEACON_FACET_GROUP)
                        .type(Filter.TypeEnum.VARIANT)
                        .key("Mutations in:")
                        .label("Mutations in:")
                        .inputs(FilterInputs
                                .builder()
                                .entries(it)
                                .build())
                        .build()).toList();
    }

    private List<List<FilterInputEntry>> buildEntries() {
        return List.of(
                List.of(
                        FilterInputEntry
                                .builder()
                                .id("gene")
                                .label("gene")
                                .build(),
                        FilterInputEntry.builder()
                                .id("aminoacid")
                                .label("aminoacid")
                                .build()
                ),
                List.of(
                        FilterInputEntry
                                .builder()
                                .id("gene")
                                .label("gene")
                                .build()
                ),
                List.of(
                        FilterInputEntry
                                .builder()
                                .id("alternateBases")
                                .label("alternateBases")
                                .build(),
                        FilterInputEntry.builder()
                                .id("referenceBases")
                                .label("referenceBases")
                                .build(),
                        FilterInputEntry.builder()
                                .id("start")
                                .label("start")
                                .build(),
                        FilterInputEntry.builder()
                                .id("end")
                                .label("end")
                                .build(),
                        FilterInputEntry.builder()
                                .id("type")
                                .label("type")
                                .build()
                ),
                List.of(
                        FilterInputEntry
                                .builder()
                                .id("alternateBases")
                                .label("alternateBases")
                                .build(),
                        FilterInputEntry.builder()
                                .id("referenceBases")
                                .label("referenceBases")
                                .build(),
                        FilterInputEntry.builder()
                                .id("start")
                                .label("start")
                                .build(),
                        FilterInputEntry.builder()
                                .id("end")
                                .label("end")
                                .build(),
                        FilterInputEntry.builder()
                                .id("type")
                                .label("type")
                                .build(),
                        FilterInputEntry.builder()
                                .id("assemblyId")
                                .label("assemblyId")
                                .build()
                ));
    }
}
