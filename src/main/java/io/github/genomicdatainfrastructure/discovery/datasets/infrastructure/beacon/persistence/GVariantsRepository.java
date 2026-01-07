// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.GVariantsRepositoryPort;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.GVariantAlleleFrequencyResponse;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.gvariants.api.GVariantsApi;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@ApplicationScoped
@LookupIfProperty(name = "sources.beacon", stringValue = "true")
public class GVariantsRepository implements GVariantsRepositoryPort {

    private final GVariantsApi gVariantsApi;
    private final GoEPopulationPatternExtractor populationExtractor;

    @Inject
    public GVariantsRepository(@RestClient GVariantsApi gVariantsApi,
            GoEPopulationPatternExtractor populationExtractor) {
        this.gVariantsApi = gVariantsApi;
        this.populationExtractor = populationExtractor;
    }

    @Override
    public GVariantAlleleFrequencyResponse search(GVariantSearchQuery query) {
        var beaconQuery = BeaconGVariantsRequestMapper.map(query);
        if (isEmpty(beaconQuery.getQuery().getRequestParameters())) {
            return new GVariantAlleleFrequencyResponse();
        }

        var populationFilter = BeaconGVariantsRequestMapper.extractPopulationFilter(query);
        var response = gVariantsApi.postGenomicVariationsRequest(beaconQuery);
        var results = BeaconGVariantsRequestMapper.map(response);
        var filteredResults = BeaconGVariantsRequestMapper.filterByPopulation(results,
                populationFilter);

        return BeaconGVariantsRequestMapper.mapToAlleleFrequencyResponse(filteredResults,
                populationExtractor);
    }
}
