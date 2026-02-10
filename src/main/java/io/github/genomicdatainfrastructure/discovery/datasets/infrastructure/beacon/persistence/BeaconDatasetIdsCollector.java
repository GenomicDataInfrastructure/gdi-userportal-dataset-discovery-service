// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.auth.BeaconAuth;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.api.BeaconQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconResponseContent;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconResultSet;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.java.Log;
import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Log
@ApplicationScoped
@LookupIfProperty(name = "sources.beacon", stringValue = "true")
public class BeaconDatasetIdsCollector implements DatasetIdsCollector {

    private static final String BEACON_DATASET_TYPE = "dataset";
    private static final String BEACON_QUERY_ERROR_MSG = "Beacon query failed with error, skipping beacon results. Status: %d, Message: %s";
    private static final String BEACON_QUERY_UNEXPECTED_ERROR_MSG = "Unexpected error during beacon query, skipping beacon results: %s";

    private static final ThreadLocal<String> lastError = new ThreadLocal<>();

    private final BeaconQueryApi beaconQueryApi;
    private final BeaconAuth beaconAuth;

    @Inject
    public BeaconDatasetIdsCollector(@RestClient BeaconQueryApi beaconQueryApi,
            BeaconAuth beaconAuth) {
        this.beaconQueryApi = beaconQueryApi;
        this.beaconAuth = beaconAuth;
    }

    @Override
    public Map<String, Integer> collect(DatasetSearchQuery query, String accessToken) {
        lastError.remove();

        var beaconAuthorization = beaconAuth.retrieveAuthorization(accessToken);

        var beaconQuery = BeaconIndividualsRequestMapper.from(query);

        if (beaconAuthorization == null || (isEmpty(beaconQuery.getQuery().getFilters()) &&
                isEmpty(beaconQuery.getQuery().getRequestParameters()))) {
            return null;
        }

        try {
            var response = beaconQueryApi.listIndividuals(beaconAuthorization, beaconQuery);

            var nonNullResultSets = ofNullable(response)
                    .map(BeaconResponse::getResponse)
                    .map(BeaconResponseContent::getResultSets)
                    .filter(ObjectUtils::isNotEmpty)
                    .orElseGet(List::of);

            return nonNullResultSets.stream()
                    .filter(Objects::nonNull)
                    .filter(it -> BEACON_DATASET_TYPE.equals(it.getSetType()))
                    .filter(it -> isNotBlank(it.getId()))
                    .filter(it -> it.getResultsCount() != null && it.getResultsCount() > 0)
                    .collect(toMap(BeaconResultSet::getId, BeaconResultSet::getResultsCount));
        } catch (WebApplicationException exception) {
            int status = exception.getResponse().getStatus();
            String message = exception.getMessage();
            log.log(Level.WARNING,
                    String.format(BEACON_QUERY_ERROR_MSG, status, message));
            log.log(Level.WARNING, exception, exception::getMessage);
            lastError.set(String.format("Beacon service error (HTTP %d): %s", status, message));
            return null;
        } catch (Exception exception) {
            log.log(Level.SEVERE,
                    String.format(BEACON_QUERY_UNEXPECTED_ERROR_MSG, exception.getMessage()));
            log.log(Level.SEVERE, exception, exception::getMessage);
            lastError.set("Beacon service unavailable: " + exception.getMessage());
            return null;
        }
    }

    public String getLastError() {
        String error = lastError.get();
        lastError.remove();
        return error;
    }
}
