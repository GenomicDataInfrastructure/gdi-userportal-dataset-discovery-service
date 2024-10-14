package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon.strategies;

import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconFilteringTerm;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconFilteringTermsResponseContent;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconResource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon.strategies.FilterStrategyContext.BEACON_FACET_GROUP;
import static io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon.strategies.FilterStrategyContext.SUPPORTED_FILTER_SCOPE;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class OntologyFilterStrategy implements BeaconFilterStrategy {

    private static final String DEFAULT_TYPE = "ontology";

    @Override
    public List<Filter> buildFilters(BeaconFilteringTermsResponseContent content) {
        var termsGroupedById = groupTermsById(content);
        var resourceNamesMappedById = mapResourceNamesById(content);
        return toFilters(termsGroupedById, resourceNamesMappedById);
    }

    private Map<String, List<ValueLabel>> groupTermsById(
            BeaconFilteringTermsResponseContent response
    ) {
        return response.getFilteringTerms().stream()
                .filter(Objects::nonNull)
                .filter(it -> isNotBlank(it.getLabel()))
                .filter(it -> isNotBlank(it.getId()))
                .filter(it -> it.getId().contains(":"))
                .filter(it -> DEFAULT_TYPE.equals(it.getType()))
                .filter(it -> isNotEmpty(it.getScopes()))
                .filter(it -> it.getScopes().contains(SUPPORTED_FILTER_SCOPE))
                .collect(groupingBy(
                        it -> it.getId().split(":")[0].toLowerCase(),
                        mapping(this::mapFilteringTermToValueLabel, toList())
                ));
    }

    private ValueLabel mapFilteringTermToValueLabel(BeaconFilteringTerm term) {
        return ValueLabel.builder()
                .value(term.getId())
                .label(term.getLabel())
                .build();
    }

    private Map<String, String> mapResourceNamesById(
            BeaconFilteringTermsResponseContent response
    ) {
        return response.getResources().stream()
                .filter(it -> isNotBlank(it.getId()))
                .filter(it -> isNotBlank(it.getName()))
                .collect(toMap(
                        BeaconResource::getId,
                        BeaconResource::getName
                ));
    }

    private List<Filter> toFilters(
            Map<String, List<ValueLabel>> termsGroupedById,
            Map<String, String> resourceNamesMappedById
    ) {
        return termsGroupedById.entrySet().stream()
                .filter(entry -> resourceNamesMappedById.containsKey(entry.getKey()))
                .map(entry -> Filter.builder()
                        .source(BEACON_FACET_GROUP)
                        .type(Filter.TypeEnum.DROPDOWN)
                        .key(entry.getKey())
                        .label(resourceNamesMappedById.get(entry.getKey()))
                        .values(entry.getValue())
                        .build())
                .toList();
    }
}
