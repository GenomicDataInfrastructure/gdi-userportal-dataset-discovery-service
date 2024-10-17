// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.auth.BeaconAuth;
import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FilterBuilder;
import io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon.strategies.FilterStrategyContext;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.api.BeaconQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconFilteringTermsResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconFilteringTermsResponseContent;
import io.quarkus.arc.lookup.LookupIfProperty;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@ApplicationScoped
@LookupIfProperty(name = "sources.beacon", stringValue = "true")
public class BeaconFilterBuilder implements FilterBuilder {

    private final BeaconQueryApi beaconQueryApi;
    private final BeaconAuth beaconAuth;
    private FilterStrategyContext context;

    @Inject
    public BeaconFilterBuilder(@RestClient BeaconQueryApi beaconQueryApi, BeaconAuth beaconAuth,
            FilterStrategyContext context) {
        this.beaconQueryApi = beaconQueryApi;
        this.beaconAuth = beaconAuth;
        this.context = context;
    }

    @Override
    @CacheResult(cacheName = "beacon-facets-cache")
    public List<Filter> build(String accessToken) {
        var beaconAuthorization = beaconAuth.retrieveAuthorization(accessToken);
        if (beaconAuthorization == null) {
            return List.of();
        }
        var filteringTermsResponse = retrieveNonNullFilteringTermsResponse(beaconAuthorization);

        return context.buildFilters(filteringTermsResponse);
    }

    private BeaconFilteringTermsResponseContent retrieveNonNullFilteringTermsResponse(
            String authorization) {
        try {
            String filteringTermsResponse = new String(Files.readAllBytes(Paths.get(
                    "src/main/resources/mock-beacon-filtering-terms.json")));
            var objectMapper = new ObjectMapper();
            var filteringTerms = objectMapper.readValue(filteringTermsResponse,
                    BeaconFilteringTermsResponse.class);

            return ofNullable(filteringTerms)
                    .map(BeaconFilteringTermsResponse::getResponse)
                    .filter(it -> isNotEmpty(it.getFilteringTerms()))
                    .filter(it -> isNotEmpty(it.getResources()))
                    .orElseGet(BeaconFilteringTermsResponseContent::new);

        } catch (IOException e) {
            return null;
        }
    }
}
