// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.RecordsCountCollector;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.auth.BeaconAuth;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.api.BeaconQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconIndividualsResponseContent;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconResultSet;
import io.github.genomicdatainfrastructure.discovery.utils.BeaconIndividualsRequestMapper;
import io.quarkus.arc.lookup.LookupIfProperty;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@ApplicationScoped
@LookupIfProperty(name = "sources.beacon", stringValue = "true")
public class BeaconDatasetIdsCollector implements DatasetIdsCollector, RecordsCountCollector {

    private static final String BEACON_DATASET_TYPE = "dataset";

    private final BeaconQueryApi beaconQueryApi;
    private final BeaconAuth beaconAuth;

    @Inject
    public BeaconDatasetIdsCollector(@RestClient BeaconQueryApi beaconQueryApi,
            BeaconAuth beaconAuth) {
        this.beaconQueryApi = beaconQueryApi;
        this.beaconAuth = beaconAuth;
    }

    @Override
    public List<String> collect(DatasetSearchQuery query, String accessToken) {
        var recordsCount = collectRecordsCount(query, accessToken);

        var datasetIds = ofNullable(recordsCount)
                .map(Map::keySet)
                .map(it -> it.stream().toList())
                .orElse(null);

        return datasetIds;
    }

    @CacheResult(cacheName = "beacon-result-sets")
    @Override
    public Map<String, Integer> collectRecordsCount(DatasetSearchQuery query, String accessToken) {
        var beaconAuthorization = beaconAuth.retrieveAuthorization(accessToken);

        var beaconQuery = BeaconIndividualsRequestMapper.from(query);

        if (beaconAuthorization == null || beaconQuery.getQuery().getFilters().isEmpty()) {
            return null;
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
                .collect(toMap(BeaconResultSet::getId, BeaconResultSet::getResultsCount));
    }
}
