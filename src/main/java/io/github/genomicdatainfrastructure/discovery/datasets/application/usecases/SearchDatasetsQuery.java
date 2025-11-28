// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.application.usecases;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence.CkanDatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Math.min;
import static java.util.Objects.nonNull;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SearchDatasetsQuery {

    private final DatasetsRepository repository;
    private final Instance<DatasetIdsCollector> collectors;

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
        var ckanCollector = collectors.stream()
                .filter(collector -> collector.getClass()
                        .getSimpleName()
                        .equals("CkanDatasetIdsCollector"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("CKAN collector not found"));

        var datasetIds = ckanCollector.collect(query, accessToken);

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
        var datasetIdsByRecordCount = collectors
                .stream()
                .map(collector -> collector.collect(query, accessToken))
                .filter(Objects::nonNull)
                .reduce(this::findIdsIntersection)
                .orElseGet(Map::of);

        var datasets = repository.search(datasetIdsByRecordCount.keySet(),
                query.getSort(),
                query.getRows(),
                query.getStart(),
                accessToken,
                preferredLanguage);

        var enhancedDatasets = datasets
                .stream()
                .map(dataset -> dataset
                        .toBuilder()
                        .recordsCount(datasetIdsByRecordCount.get(dataset.getIdentifier()))
                        .build())
                .toList();

        return DatasetsSearchResponse
                .builder()
                .count(datasetIdsByRecordCount.size())
                .results(enhancedDatasets)
                .build();
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
