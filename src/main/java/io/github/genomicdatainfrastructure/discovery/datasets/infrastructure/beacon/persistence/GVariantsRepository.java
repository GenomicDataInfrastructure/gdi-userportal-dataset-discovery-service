// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.GVariantsRepositoryPort;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.auth.BeaconAuth;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.GVariantsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.api.GVariantsApi;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@ApplicationScoped
@LookupIfProperty(name = "sources.beacon", stringValue = "true")
public class GVariantsRepository implements GVariantsRepositoryPort {

    private final GVariantsApi gVariantsApi;
    private final BeaconAuth beaconAuth;

    @Inject
    public GVariantsRepository(@RestClient GVariantsApi gVariantsApi,
            BeaconAuth beaconAuth) {
        this.gVariantsApi = gVariantsApi;
        this.beaconAuth = beaconAuth;
    }

    @Override
    public List<GVariantsSearchResponse> search(GVariantSearchQuery query,
            String accessToken) {
        var beaconAuthorization = beaconAuth.retrieveAuthorization(accessToken);

        var beaconQuery = BeaconGVariantsRequestMapper.map(query);
        if (beaconAuthorization == null || (isEmpty(beaconQuery.getQuery()
                .getRequestParameters()))) {
            return Collections.emptyList();
        }

        var response = gVariantsApi.postGenomicVariationsRequest(beaconAuthorization, beaconQuery);

        return BeaconGVariantsRequestMapper.map(response);
    }
}
