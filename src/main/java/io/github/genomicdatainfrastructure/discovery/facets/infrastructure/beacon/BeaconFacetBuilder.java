// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.facets.infrastructure.beacon;

import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.auth.BeaconAuth;
import io.github.genomicdatainfrastructure.discovery.facets.ports.IFacetBuilder;
import io.github.genomicdatainfrastructure.discovery.model.Facet;
import io.github.genomicdatainfrastructure.discovery.services.BeaconFilteringTermsService;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
@LookupIfProperty(name = "sources.beacon", stringValue = "true")
public class BeaconFacetBuilder implements IFacetBuilder {

    private final BeaconFilteringTermsService service;
    private final BeaconAuth beaconAuth;

    @Inject
    public BeaconFacetBuilder(BeaconFilteringTermsService service, BeaconAuth beaconAuth) {
        this.service = service;
        this.beaconAuth = beaconAuth;
    }

    @Override
    public List<Facet> build(String accessToken) {
        var beaconAuthorization = beaconAuth.retrieveAuthorization(accessToken);
        if (beaconAuthorization == null) {
            return null;
        }
        return service.listFilteringTermList(beaconAuthorization);
    }
}
