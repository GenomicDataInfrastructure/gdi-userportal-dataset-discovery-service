// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.application.usecases;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.BeaconDatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence.CkanDatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FilterBuilder;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_FILTER_SOURCE;
import static java.lang.Math.min;
import static java.util.Objects.nonNull;

@Log
@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SearchDatasetsQuery {

    private final DatasetsRepository repository;
    private final BeaconDatasetIdsCollector beaconDatasetIdsCollector;
    private final CkanDatasetIdsCollector ckanDatasetIdsCollector;
    private final Instance<FilterBuilder> filterBuilders;

    public DatasetsSearchResponse execute(DatasetSearchQuery query, String accessToken,
            String preferredLanguage) {
        boolean includeBeacon = query.getIncludeBeacon() == null || query.getIncludeBeacon();

        if (!includeBeacon) {
            return enrichWithSupplementalFacets(
                    searchCkanOnly(query, accessToken, preferredLanguage),
                    accessToken,
                    preferredLanguage);
        }

        return enrichWithSupplementalFacets(
                searchWithBeacon(query, accessToken, preferredLanguage),
                accessToken,
                preferredLanguage);
    }

    /**
     * Fast CKAN-only search without Beacon
     */
    private DatasetsSearchResponse searchCkanOnly(DatasetSearchQuery query, String accessToken,
            String preferredLanguage) {

        var searchResult = repository.search(query, accessToken, preferredLanguage);

        return searchResult;
    }

    /**
     * Comprehensive search with Beacon (intersection)
     */
    private DatasetsSearchResponse searchWithBeacon(DatasetSearchQuery query, String accessToken,
            String preferredLanguage) {

        // Collect CKAN datasets
        Map<String, Integer> ckanDatasetIds = ckanDatasetIdsCollector.collect(query, accessToken);

        // Try to collect Beacon datasets and capture any errors
        Map<String, Integer> beaconDatasetIds = null;
        String beaconError = null;

        try {
            beaconDatasetIds = beaconDatasetIdsCollector.collect(query, accessToken);
        } catch (WebApplicationException exception) {
            int status = exception.getResponse().getStatus();

            beaconError = createBeaconErrorMessage(status);
            log.log(Level.WARNING, exception.getMessage(), exception);
        }

        // Calculate intersection: if beacon failed, use CKAN-only results
        Map<String, Integer> datasetIdsByRecordCount;
        if (beaconDatasetIds != null) {
            datasetIdsByRecordCount = findIdsIntersection(ckanDatasetIds, beaconDatasetIds);
        } else {
            // Beacon failed, fall back to CKAN-only results
            datasetIdsByRecordCount = ckanDatasetIds;
        }

        var searchResult = repository.search(datasetIdsByRecordCount.keySet(),
                query.getSort(),
                query.getRows(),
                query.getStart(),
                accessToken,
                preferredLanguage);

        var enhancedDatasets = searchResult.getResults()
                .stream()
                .filter(dataset -> datasetIdsByRecordCount.containsKey(dataset.getIdentifier()))
                .map(dataset -> dataset
                        .toBuilder()
                        .recordsCount(datasetIdsByRecordCount.get(dataset.getIdentifier()))
                        .build())
                .toList();

        return DatasetsSearchResponse
                .builder()
                .count(searchResult.getCount())
                .results(enhancedDatasets)
                .facets(searchResult.getFacets())
                .beaconError(beaconError)
                .build();
    }

    /**
     * Create user-friendly error message based on HTTP status code
     */
    private String createBeaconErrorMessage(int status) {
        return switch (status) {
            case 401, 403 ->
                "The user is not recognised as a Researcher or may not have accepted the latest version of the terms and conditions for subject-level data discovery, please contact the signing official of your home organisation.";
            default ->
                "An unexpected remote exception has happened, please try again. If the error persists, please report it to the helpdesk.";
        };
    }

    private Map<String, Integer> findIdsIntersection(Map<String, Integer> a,
            Map<String, Integer> b) {
        var newMap = new HashMap<String, Integer>();
        for (var entryA : a.entrySet()) {

            if (b.containsKey(entryA.getKey())) {
                var recordCountA = entryA.getValue();
                var recordCountB = b.get(entryA.getKey());

                var newRecordCount = recordCountA;
                if (nonNull(recordCountA) && nonNull(recordCountB)) {
                    newRecordCount = min(recordCountA, recordCountB);
                } else if (nonNull(recordCountB)) {
                    newRecordCount = recordCountB;
                }

                newMap.put(entryA.getKey(), newRecordCount);
            }
        }

        return newMap;
    }

    private DatasetsSearchResponse enrichWithSupplementalFacets(DatasetsSearchResponse response,
            String accessToken,
            String preferredLanguage) {
        var supplementalFacets = filterBuilders.stream()
                .filter(filterBuilder -> !Objects.equals(CKAN_FILTER_SOURCE, filterBuilder
                        .source()))
                .map(filterBuilder -> filterBuilder.build(accessToken, preferredLanguage))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();

        if (supplementalFacets.isEmpty()) {
            return response;
        }

        var mergedFacets = new java.util.ArrayList<>(
                response.getFacets() != null ? response.getFacets() : List.of());
        mergedFacets.addAll(supplementalFacets);

        return response.toBuilder()
                .facets(List.copyOf(mergedFacets))
                .build();
    }
}
