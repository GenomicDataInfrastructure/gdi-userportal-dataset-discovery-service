// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.auth.BeaconAuth;
import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FilterBuilder;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.List;

import static java.util.Optional.ofNullable;

@ApplicationScoped
@LookupIfProperty(name = "sources.beacon", stringValue = "true")
public class BeaconFilterBuilder implements FilterBuilder {

    private final BeaconAuth beaconAuth;
    private final ObjectMapper objectMapper;

    @Inject
    public BeaconFilterBuilder(BeaconAuth beaconAuth,
            ObjectMapper objectMapper) {
        this.beaconAuth = beaconAuth;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public List<Filter> build(final String accessToken) {
        final var beaconAuthorization = beaconAuth.retrieveAuthorization(accessToken);
        if (beaconAuthorization == null) {
            return List.of();
        }
        final var inputStream = ofNullable(getClass()
                .getClassLoader()
                .getResourceAsStream("mock-beacon-filters.json"))
                .orElseThrow(() -> new IOException(
                        "Resource file 'mock-beacon-filters.json' not found"));

        return objectMapper.readValue(inputStream,
                new TypeReference<>() {
                });
    }
}
