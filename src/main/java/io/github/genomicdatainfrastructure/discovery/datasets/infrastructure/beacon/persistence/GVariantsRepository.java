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

import java.util.Collections;
import java.util.List;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.POPULATION_PATTERN;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

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
        if (isEmpty(beaconQuery.getQuery().getRequestParameters())) {
            return Collections.emptyList();
        }

        var response = gVariantsApi.postGenomicVariationsRequest(beaconQuery);
        var unfilteredResponse = BeaconGVariantsRequestMapper.map(response);

        return filterPopulationByCountryOfBirthAndSex(unfilteredResponse, query);
    }

    private List<GVariantsSearchResponse> filterPopulationByCountryOfBirthAndSex(
            List<GVariantsSearchResponse> variants, GVariantSearchQuery query) {

        var params = query.getParams();
        if (params == null) {
            return variants;
        }
        
        String countryOfBirth = params.getCountryOfBirth();
        String sex = params.getSex();

        if (isEmpty(countryOfBirth) && isEmpty(sex)) {
            return variants;
        }

        return variants.stream()
                .filter(v -> matchesFilters(v, countryOfBirth, sex))
                .toList();
    }

    private boolean matchesFilters(GVariantsSearchResponse variant, String countryOfBirth,
            String sex) {
        if (variant.getPopulation() == null) {
            return false;
        }

        var matcher = POPULATION_PATTERN.matcher(variant.getPopulation());
        if (!matcher.matches()) {
            return false;
        }

        String extractedCountry = matcher.group(1);
        String sexAfterCountry = matcher.group(2);
        String sexOnly = matcher.group(3);
        String extractedSex = sexAfterCountry != null ? sexAfterCountry : sexOnly;

        if (!isEmpty(countryOfBirth) &&
                !countryOfBirth.toUpperCase().equals(extractedCountry)) {
            return false;
        }

        if (!isEmpty(sex) &&
                !sex.toUpperCase().equals(extractedSex)) {
            return false;
        }

        return true;
    }
}
