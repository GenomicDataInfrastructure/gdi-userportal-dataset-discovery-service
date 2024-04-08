// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static java.util.stream.Collectors.toMap;
import static io.github.genomicdatainfrastructure.discovery.services.PackagesSearchResponseMapper.CKAN_FACET_GROUP;
import static io.github.genomicdatainfrastructure.discovery.services.BeaconFilteringTermsService.BEACON_FACET_GROUP;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.model.FacetGroup;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.api.BeaconQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsRequest;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsResponseContent;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconResultSet;
import io.github.genomicdatainfrastructure.discovery.remote.keycloak.api.KeycloakQueryApi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Objects;
import java.util.HashMap;
import java.util.List;

@ApplicationScoped
public class BeaconDatasetsSearchService implements DatasetsSearchService {

    private static final String BEACON_IDP_ALIAS = "LSAAI";
    private static final String BEARER_PATTERN = "Bearer %s";
    private static final String BEACON_DATASET_TYPE = "dataset";
    private static final String CKAN_IDENTIFIER_FIELD = "identifier";

    private final BeaconQueryApi beaconQueryApi;
    private final KeycloakQueryApi keycloakQueryApi;
    private final CkanDatasetsSearchService datasetsSearchService;
    private final BeaconFilteringTermsService beaconFilteringTermsService;

    @Inject
    public BeaconDatasetsSearchService(
            @RestClient BeaconQueryApi beaconQueryApi,
            @RestClient KeycloakQueryApi keycloakQueryApi,
            CkanDatasetsSearchService datasetsSearchService,
            BeaconFilteringTermsService beaconFilteringTermsService
    ) {
        this.beaconQueryApi = beaconQueryApi;
        this.keycloakQueryApi = keycloakQueryApi;
        this.datasetsSearchService = datasetsSearchService;
        this.beaconFilteringTermsService = beaconFilteringTermsService;
    }

    @Override
    public DatasetsSearchResponse search(DatasetSearchQuery query, String accessToken) {
        var beaconQuery = BeaconIndividualsRequestMapper.from(query);

        if (accessToken == null || beaconQuery.getQuery().getFilters().isEmpty()) {
            return datasetsSearchService.search(query, accessToken);
        }

        var beaconAuthorization = retrieveBeaconAuthorization(accessToken);

        var beaconResponse = queryOnBeacon(beaconAuthorization, beaconQuery);

        var enhancedQuery = enhanceQueryFacets(query, beaconResponse);

        var datasetsReponse = datasetsSearchService.search(enhancedQuery, accessToken);

        return enhanceDatasetsResponse(beaconAuthorization, datasetsReponse, beaconResponse);
    }

    private String retrieveBeaconAuthorization(String accessToken) {
        var keycloakAuthorization = BEARER_PATTERN.formatted(accessToken);
        var response = keycloakQueryApi.retriveIdpTokens(BEACON_IDP_ALIAS, keycloakAuthorization);
        return BEARER_PATTERN.formatted(response.getAccessToken());
    }

    private List<BeaconResultSet> queryOnBeacon(
            String beaconAuthorization,
            BeaconIndividualsRequest beaconQuery
    ) {

        var response = beaconQueryApi.listIndividuals(beaconAuthorization, beaconQuery);

        var nonNullResultSets = ofNullable(response)
                .map(BeaconIndividualsResponse::getResponse)
                .map(BeaconIndividualsResponseContent::getResultSets)
                .filter(ObjectUtils::isNotEmpty)
                .orElseGet(List::of);

        return nonNullResultSets.stream()
                .filter(Objects::nonNull)
                .filter(it -> BEACON_DATASET_TYPE.equals(it.getSetType()))
                .filter(it -> isNotBlank(it.getId()))
                .filter(it -> it.getResultsCount() > 0)
                .toList();
    }

    private DatasetSearchQuery enhanceQueryFacets(
            DatasetSearchQuery query,
            List<BeaconResultSet> resultSets
    ) {
        var enhancedFacets = resultSets.stream()
                .map(BeaconResultSet::getId)
                .map(it -> DatasetSearchQueryFacet.builder()
                        .facetGroup(CKAN_FACET_GROUP)
                        .facet(CKAN_IDENTIFIER_FIELD)
                        .value(it)
                        .build())
                .collect(toCollection(ArrayList::new));

        if (query.getFacets() != null) {
            enhancedFacets.addAll(query.getFacets());
        }

        return query.toBuilder()
                .facets(enhancedFacets)
                .build();
    }

    private DatasetsSearchResponse enhanceDatasetsResponse(
            String beaconAuthorization,
            DatasetsSearchResponse datasetsReponse,
            List<BeaconResultSet> resultSets
    ) {
        var facetGroupCount = new HashMap<String, Integer>();
        facetGroupCount.put(BEACON_FACET_GROUP, resultSets.size());
        if (isNotEmpty(datasetsReponse.getFacetGroupCount())) {
            facetGroupCount.putAll(datasetsReponse.getFacetGroupCount());
        }

        var facetGroups = new ArrayList<FacetGroup>();
        facetGroups.add(beaconFilteringTermsService.listFilteringTerms(beaconAuthorization));
        if (isNotEmpty(datasetsReponse.getFacetGroups())) {
            facetGroups.addAll(datasetsReponse.getFacetGroups());
        }

        var results = List.<SearchedDataset>of();
        if (isNotEmpty(datasetsReponse.getResults())) {
            var recordCounts = resultSets.stream()
                    .collect(toMap(
                            BeaconResultSet::getId,
                            BeaconResultSet::getResultsCount
                    ));

            results = datasetsReponse.getResults()
                    .stream()
                    .map(it -> it.toBuilder()
                            .recordsCount(recordCounts.get(it.getIdentifier()))
                            .build())
                    .toList();
        }

        return datasetsReponse.toBuilder()
                .facetGroupCount(facetGroupCount)
                .facetGroups(facetGroups)
                .results(results)
                .build();
    }
}
