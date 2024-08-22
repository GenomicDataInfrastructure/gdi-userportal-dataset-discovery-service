// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infra.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.FacetsBuilder;
import io.github.genomicdatainfrastructure.discovery.datasets.infra.beacon.auth.BeaconAuth;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.FacetGroup;
import io.github.genomicdatainfrastructure.discovery.services.BeaconFilteringTermsService;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@LookupIfProperty(name = "sources.beacon", stringValue = "true")
public class BeaconFacetsBuilder implements FacetsBuilder {

    private final BeaconFilteringTermsService service;
    private final BeaconAuth beaconAuth;

    @Inject
    public BeaconFacetsBuilder(BeaconFilteringTermsService service, BeaconAuth beaconAuth) {
        this.service = service;
        this.beaconAuth = beaconAuth;
    }

    @Override
    public FacetGroup build(DatasetSearchQuery query, String accessToken) {
        var beaconAuthorization = beaconAuth.retrieveAuthorization(accessToken);
        if (beaconAuthorization == null) {
            return null;
        }
        return service.listFilteringTerms(beaconAuthorization);
    }
}