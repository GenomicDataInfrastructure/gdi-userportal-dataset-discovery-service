// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.repositories;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static java.util.stream.Collectors.toMap;
import static io.github.genomicdatainfrastructure.discovery.utils.PackagesSearchResponseMapper.CKAN_FACET_GROUP;
import static io.github.genomicdatainfrastructure.discovery.services.BeaconFilteringTermsService.BEACON_FACET_GROUP;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.services.*;
import io.github.genomicdatainfrastructure.discovery.utils.BeaconIndividualsRequestMapper;
import io.github.genomicdatainfrastructure.discovery.utils.CkanFacetsQueryBuilder;
import io.github.genomicdatainfrastructure.discovery.utils.PackagesSearchResponseMapper;
import io.quarkus.arc.lookup.LookupIfProperty;
import lombok.extern.java.Log;
import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.api.BeaconQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsResponseContent;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconResultSet;
import io.github.genomicdatainfrastructure.discovery.remote.keycloak.api.KeycloakQueryApi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

import java.util.ArrayList;
import java.util.Objects;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

@Log
@LookupIfProperty(name = "sources.beacon", stringValue = "true")
@ApplicationScoped
public class BeaconDatasetsRepository implements DatasetsRepository {

    private static final String SELECTED_FACETS = "[\"access_rights\",\"theme\",\"tags\",\"spatial_uri\",\"organization\",\"publisher_name\",\"res_format\"]";
    private final CkanQueryApi ckanQueryApi;
    private static final Set<Integer> SKIP_BEACON_QUERY_STATUS = Set.of(400, 401, 403);
    private static final String BEACON_ACCESS_TOKEN_INFO = "Skipping beacon search, user is not authorized or the token is invalid.";
    private static final String BEARER_PATTERN = "Bearer %s";
    private static final String BEACON_DATASET_TYPE = "dataset";
    private static final String CKAN_IDENTIFIER_FIELD = "identifier";
    private final BeaconQueryApi beaconQueryApi;
    private final String beaconIdpAlias;
    private final KeycloakQueryApi keycloakQueryApi;
    private final BeaconFilteringTermsService beaconFilteringTermsService;

    @Inject
    public BeaconDatasetsRepository(
            @RestClient CkanQueryApi ckanQueryApi,
            @RestClient BeaconQueryApi beaconQueryApi,
            @ConfigProperty(name = "quarkus.rest-client.keycloak_yaml.beacon_idp_alias") String beaconIdpAlias,
            @RestClient KeycloakQueryApi keycloakQueryApi,
            BeaconFilteringTermsService beaconFilteringTermsService
    ) {
        this.ckanQueryApi = ckanQueryApi;
        this.beaconQueryApi = beaconQueryApi;
        this.beaconIdpAlias = beaconIdpAlias;
        this.keycloakQueryApi = keycloakQueryApi;
        this.beaconFilteringTermsService = beaconFilteringTermsService;
    }

    @Override
    public DatasetsSearchResponse search(DatasetSearchQuery query, String accessToken) {
        var beaconAuthorization = retrieveBeaconAuthorization(accessToken);

        if (beaconAuthorization == null) {
            return searchCkan(query, accessToken);
        }

        var resultSets = queryOnBeaconIfThereAreBeaconFilters(beaconAuthorization, query);

        var datasetsSearchResponse = queryOnCkanIfThereIsNoBeaconFilterOrResultsetsIsNotEmpty(
                accessToken,
                query,
                resultSets
        );

        return enhanceDatasetsResponse(beaconAuthorization, datasetsSearchResponse, resultSets);
    }

    private String retrieveBeaconAuthorization(String accessToken) {
        if (accessToken == null) {
            return null;
        }

        var keycloakAuthorization = BEARER_PATTERN.formatted(accessToken);
        try {
            var response = keycloakQueryApi.retriveIdpTokens(beaconIdpAlias, keycloakAuthorization);
            return BEARER_PATTERN.formatted(response.getAccessToken());
        } catch (WebApplicationException exception) {
            if (SKIP_BEACON_QUERY_STATUS.contains(exception.getResponse().getStatus())) {
                log.log(Level.INFO, BEACON_ACCESS_TOKEN_INFO);
                log.log(Level.WARNING, exception, exception::getMessage);
                return null;
            }
            throw exception;
        }
    }

    private List<BeaconResultSet> queryOnBeaconIfThereAreBeaconFilters(
            String beaconAuthorization,
            DatasetSearchQuery query
    ) {
        var beaconQuery = BeaconIndividualsRequestMapper.from(query);
        if (beaconQuery.getQuery().getFilters().isEmpty()) {
            return List.of();
        }

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
                .filter(it -> it.getResultsCount() != null && it.getResultsCount() > 0)
                .toList();
    }

    private DatasetsSearchResponse queryOnCkanIfThereIsNoBeaconFilterOrResultsetsIsNotEmpty(
            String ckanAuthorization,
            DatasetSearchQuery query,
            List<BeaconResultSet> resultSets
    ) {
        var nonNullFacets = ofNullable(query.getFacets()).orElseGet(List::of);
        var thereIsAtLeastOneBeaconFilter = nonNullFacets.stream()
                .anyMatch(it -> BEACON_FACET_GROUP.equals(it.getFacetGroup()));

        if (thereIsAtLeastOneBeaconFilter && resultSets.isEmpty()) {
            return DatasetsSearchResponse.builder()
                    .count(0)
                    .build();
        }

        var enhancedQuery = enhanceQueryFacets(query, resultSets);
        return searchCkan(enhancedQuery, ckanAuthorization);
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
            DatasetsSearchResponse datasetsSearchResponse,
            List<BeaconResultSet> resultSets
    ) {
        var facetGroupCount = new HashMap<String, Integer>();
        facetGroupCount.put(BEACON_FACET_GROUP, resultSets.size());
        if (isNotEmpty(datasetsSearchResponse.getFacetGroupCount())) {
            facetGroupCount.putAll(datasetsSearchResponse.getFacetGroupCount());
        }

        var facetGroups = new ArrayList<FacetGroup>();
        facetGroups.add(beaconFilteringTermsService.listFilteringTerms(beaconAuthorization));
        if (isNotEmpty(datasetsSearchResponse.getFacetGroups())) {
            facetGroups.addAll(datasetsSearchResponse.getFacetGroups());
        }

        var results = List.<SearchedDataset>of();
        if (isNotEmpty(datasetsSearchResponse.getResults())) {
            var recordCounts = resultSets.stream()
                    .collect(toMap(
                            BeaconResultSet::getId,
                            BeaconResultSet::getResultsCount
                    ));

            results = datasetsSearchResponse.getResults()
                    .stream()
                    .map(it -> it.toBuilder()
                            .recordsCount(recordCounts.get(it.getIdentifier()))
                            .build())
                    .toList();
        }

        return datasetsSearchResponse.toBuilder()
                .facetGroupCount(facetGroupCount)
                .facetGroups(facetGroups)
                .results(results)
                .build();
    }

    private DatasetsSearchResponse searchCkan(DatasetSearchQuery query, String ckanAuthorization) {
        var facetsQuery = CkanFacetsQueryBuilder.buildFacetQuery(query);

        var response = ckanQueryApi.packageSearch(
                query.getQuery(),
                facetsQuery,
                query.getSort(),
                query.getRows(),
                query.getStart(),
                SELECTED_FACETS,
                ckanAuthorization
        );

        return PackagesSearchResponseMapper.from(response);
    }
}
