// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.application.usecases;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.BeaconDatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence.CkanDatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static java.lang.Math.min;
import static java.util.Objects.nonNull;

@Log
@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SearchDatasetsQuery {

    private final DatasetsRepository repository;
    private final BeaconDatasetIdsCollector beaconDatasetIdsCollector;
    private final CkanDatasetIdsCollector ckanDatasetIdsCollector;

    public DatasetsSearchResponse execute(DatasetSearchQuery query, String accessToken,
            String preferredLanguage) {
        boolean includeBeacon = query.getIncludeBeacon() == null ? true
                : query.getIncludeBeacon();

        if (!includeBeacon) {
            return searchCkanOnly(query, accessToken, preferredLanguage);
        }

        return searchWithBeacon(query, accessToken, preferredLanguage);
    }

    /**
     * Fast CKAN-only search without Beacon
     */
    private DatasetsSearchResponse searchCkanOnly(DatasetSearchQuery query, String accessToken,
            String preferredLanguage) {

        var datasetIds = ckanDatasetIdsCollector.collect(query, accessToken);

        var datasets = repository.search(datasetIds.keySet(), query.getSort(),
                query.getRows(), query.getStart(), accessToken, preferredLanguage);

        return DatasetsSearchResponse.builder()
                .count(datasetIds.size())
                .results(datasets)
                .build();
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
            String message = exception.getMessage();

            beaconError = createBeaconErrorMessage(status, message);
            log.log(Level.WARNING, String.format("Beacon query failed with HTTP %d: %s", status,
                    message), exception);
        } catch (Exception exception) {
            beaconError = "Beacon service unavailable: " + exception.getMessage();
            log.log(Level.SEVERE, "Unexpected error during beacon query: " + exception.getMessage(),
                    exception);
        }

        // Calculate intersection: if beacon failed, use CKAN-only results
        Map<String, Integer> datasetIdsByRecordCount;
        if (beaconDatasetIds != null) {
            datasetIdsByRecordCount = findIdsIntersection(ckanDatasetIds, beaconDatasetIds);
        } else {
            // Beacon failed, fall back to CKAN-only results
            datasetIdsByRecordCount = ckanDatasetIds;
        }

        var datasets = repository.search(datasetIdsByRecordCount.keySet(),
                query.getSort(),
                query.getRows(),
                query.getStart(),
                accessToken,
                preferredLanguage);

        var enhancedDatasets = datasets
                .stream()
                .filter(dataset -> datasetIdsByRecordCount.containsKey(dataset.getIdentifier()))
                .map(dataset -> dataset
                        .toBuilder()
                        .recordsCount(datasetIdsByRecordCount.get(dataset.getIdentifier()))
                        .build())
                .toList();

        return DatasetsSearchResponse
                .builder()
                .count(enhancedDatasets.size())
                .results(enhancedDatasets)
                .beaconError(beaconError)
                .build();
    }

    /**
     * Create user-friendly error message based on HTTP status code
     */
    private String createBeaconErrorMessage(int status, String originalMessage) {
        return switch (status) {
            case 401 -> "Beacon service authentication failed. Please check your credentials.";
            case 403 ->
                "Access to Beacon service denied. You may not have permission to access this resource.";
            case 404 -> "Beacon service endpoint not found. The service may be misconfigured.";
            case 500 -> "Beacon service encountered an internal error. Please try again later.";
            case 502 ->
                "Beacon service is temporarily unavailable (Bad Gateway). Please try again later.";
            case 503 ->
                "Beacon service is temporarily unavailable (Service Unavailable). Please try again later.";
            case 504 ->
                "Beacon service request timed out (Gateway Timeout). Please try again later.";
            default -> String.format("Beacon service error (HTTP %d): %s", status, originalMessage);
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
}
