// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import io.github.genomicdatainfrastructure.discovery.model.Facet;
import io.github.genomicdatainfrastructure.discovery.model.FacetGroup;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.api.BeaconQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconFilteringTerm;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconFilteringTermsResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconFilteringTermsResponseContent;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconResource;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class BeaconFilteringTermsService {

    private static final String DEFAULT_TYPE = "ontology";

    public static final String BEACON_FACET_GROUP = "beacon";

    private static final String DEFAULT_SCOPE = "individual";

    private final BeaconQueryApi beaconQueryApi;

    public BeaconFilteringTermsService(
            @RestClient BeaconQueryApi beaconQueryApi
    ) {
        this.beaconQueryApi = beaconQueryApi;
    }

    @CacheResult(cacheName = "beacon-facet-group-cache")
    public FacetGroup listFilteringTerms(String authorization) {
        var filteringTermsResponse = retrieveNonNullFilteringTermsResponse(authorization);

        var valuesGroupedByFacetId = groupValuesByFacetId(filteringTermsResponse);

        var facetIdsMappedByName = mapFacetNamesByFacetId(filteringTermsResponse);

        var facets = buildFacets(valuesGroupedByFacetId, facetIdsMappedByName);

        return FacetGroup.builder()
                .key(BEACON_FACET_GROUP)
                .label("Beacon")
                .facets(facets)
                .build();
    }

    @CacheResult(cacheName = "beacon-facets-cache")
    public List<Facet> listFilteringTermList(String authorization) {
        var filteringTermsResponse = retrieveNonNullFilteringTermsResponse(authorization);

        var valuesGroupedByFacetId = groupValuesByFacetId(filteringTermsResponse);

        var facetIdsMappedByName = mapFacetNamesByFacetId(filteringTermsResponse);

        var facets = buildFacets(valuesGroupedByFacetId, facetIdsMappedByName);

        return facets;
    }

    private BeaconFilteringTermsResponseContent retrieveNonNullFilteringTermsResponse(
            String authorization
    ) {
        var filteringTerms = beaconQueryApi.listFilteringTerms(authorization);

        return ofNullable(filteringTerms)
                .map(BeaconFilteringTermsResponse::getResponse)
                .filter(it -> isNotEmpty(it.getFilteringTerms()))
                .filter(it -> isNotEmpty(it.getResources()))
                .orElseGet(BeaconFilteringTermsResponseContent::new);
    }

    private Map<String, List<ValueLabel>> groupValuesByFacetId(
            BeaconFilteringTermsResponseContent filteringTermsResponse
    ) {
        return filteringTermsResponse.getFilteringTerms().stream()
                .filter(Objects::nonNull)
                .filter(it -> isNotBlank(it.getLabel()))
                .filter(it -> isNotBlank(it.getId()))
                .filter(it -> it.getId().contains(":"))
                .filter(it -> DEFAULT_TYPE.equals(it.getType()))
                .filter(it -> isNotEmpty(it.getScopes()))
                .filter(it -> it.getScopes().contains(DEFAULT_SCOPE))
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

    private Map<String, String> mapFacetNamesByFacetId(
            BeaconFilteringTermsResponseContent filteringTermsResponse
    ) {
        return filteringTermsResponse.getResources().stream()
                .filter(it -> isNotBlank(it.getId()))
                .filter(it -> isNotBlank(it.getName()))
                .collect(toMap(
                        BeaconResource::getId,
                        BeaconResource::getName
                ));
    }

    private List<Facet> buildFacets(
            Map<String, List<ValueLabel>> termsGroupedByType,
            Map<String, String> facetNamesMappedById
    ) {
        return termsGroupedByType.entrySet().stream()
                .filter(entry -> facetNamesMappedById.containsKey(entry.getKey()))
                .map(entry -> Facet.builder()
                        .facetGroup(BEACON_FACET_GROUP)
                        .key(entry.getKey())
                        .label(facetNamesMappedById.get(entry.getKey()))
                        .values(entry.getValue())
                        .build())
                .toList();
    }
}
