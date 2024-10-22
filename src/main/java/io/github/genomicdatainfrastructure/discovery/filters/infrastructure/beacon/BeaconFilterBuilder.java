// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.auth.BeaconAuth;
import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FilterBuilder;
import io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon.strategies.FilterStrategyContext;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconFilteringTermsResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconFilteringTermsResponseContent;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@ApplicationScoped
@LookupIfProperty(name = "sources.beacon", stringValue = "true")
public class BeaconFilterBuilder implements FilterBuilder {

    private final BeaconAuth beaconAuth;
    private final ObjectMapper objectMapper;
    private FilterStrategyContext context;

    @Inject
    public BeaconFilterBuilder(BeaconAuth beaconAuth, FilterStrategyContext context,
            ObjectMapper objectMapper) {
        this.beaconAuth = beaconAuth;
        this.context = context;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Filter> build(String accessToken) {
        var beaconAuthorization = beaconAuth.retrieveAuthorization(accessToken);
        if (beaconAuthorization == null) {
            return List.of();
        }
        var filteringTermsResponse = retrieveNonNullFilteringTermsResponse(beaconAuthorization);

        return context.buildFilters(filteringTermsResponse);
    }

    @SneakyThrows
    private BeaconFilteringTermsResponseContent retrieveNonNullFilteringTermsResponse(
            String authorization) {
        var inputStream = ofNullable(getClass()
                .getClassLoader()
                .getResourceAsStream("mock-beacon-filtering-terms.json"))
                .orElseThrow(() -> new IOException(
                        "Resource file 'mock-beacon-filtering-terms.json' not found"));

        var filteringTerms = objectMapper.readValue(inputStream,
                BeaconFilteringTermsResponse.class);

        return ofNullable(filteringTerms)
                .map(BeaconFilteringTermsResponse::getResponse)
                .filter(it -> isNotEmpty(it.getFilteringTerms()))
                .filter(it -> isNotEmpty(it.getResources()))
                .orElseGet(BeaconFilteringTermsResponseContent::new);
    }
}
