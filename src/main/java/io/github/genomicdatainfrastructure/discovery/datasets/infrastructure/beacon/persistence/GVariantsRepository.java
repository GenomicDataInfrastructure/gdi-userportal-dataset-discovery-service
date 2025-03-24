// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.GVariantsRepositoryPort;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.GVariantsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.api.GVariantsApi;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
@LookupIfProperty(name = "sources.beacon", stringValue = "true")
public class GVariantsRepository implements GVariantsRepositoryPort {

    private final GVariantsApi gVariantsApi;

    @Inject
    public GVariantsRepository(@RestClient GVariantsApi gVariantsApi) {
        this.gVariantsApi = gVariantsApi;
    }

    @Override
    public List<GVariantsSearchResponse> search(GVariantSearchQuery query) {
        var beaconQuery = BeaconGVariantsRequestMapper.map(query);

        var response = gVariantsApi.postGenomicVariationsRequest(beaconQuery);

        return BeaconGVariantsRequestMapper.map(response);
    }
}
