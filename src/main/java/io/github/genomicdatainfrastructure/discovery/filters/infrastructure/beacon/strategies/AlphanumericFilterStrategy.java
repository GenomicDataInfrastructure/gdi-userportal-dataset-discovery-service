package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon.strategies;

import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconFilteringTermsResponseContent;

import java.util.List;
import java.util.Objects;

import static io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon.strategies.FilterStrategyContext.BEACON_FACET_GROUP;
import static io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon.strategies.FilterStrategyContext.SUPPORTED_FILTER_SCOPE;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class AlphanumericFilterStrategy implements BeaconFilterStrategy {

    private static final String DEFAULT_TYPE = "alphanumeric";

    @Override
    public List<Filter> buildFilters(BeaconFilteringTermsResponseContent content) {
        return content.getFilteringTerms().stream()
                .filter(Objects::nonNull)
                .filter(it -> isNotBlank(it.getLabel()))
                .filter(it -> isNotBlank(it.getId()))
                .filter(it -> DEFAULT_TYPE.equals(it.getType()))
                .filter(it -> isNotEmpty(it.getScopes()))
                .filter(it -> it.getScopes().contains(SUPPORTED_FILTER_SCOPE))
                .map(it -> Filter.builder()
                        .source(BEACON_FACET_GROUP)
                        .type(Filter.TypeEnum.FREE_TEXT)
                        .key(it.getId())
                        .label(it.getLabel())
                        .build())
                .toList();
    }
}
