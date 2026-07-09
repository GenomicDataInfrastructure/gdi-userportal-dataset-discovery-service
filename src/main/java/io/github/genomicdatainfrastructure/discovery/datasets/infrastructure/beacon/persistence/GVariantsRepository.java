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
import java.util.Locale;
import java.util.regex.Pattern;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.PopulationConstants.hasValue;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@ApplicationScoped
@LookupIfProperty(name = "sources.beacon", stringValue = "true")
public class GVariantsRepository implements GVariantsRepositoryPort {

    private static final String ALL_FILTER_VALUE = "ALL";
    private static final Pattern COUNTRY_CODE_PATTERN = Pattern.compile("^[A-Z]{2}$");

    private final GVariantsApi gVariantsApi;

    @Inject
    public GVariantsRepository(@RestClient GVariantsApi gVariantsApi) {
        this.gVariantsApi = gVariantsApi;
    }

    @Override
    public List<GVariantsSearchResponse> search(GVariantSearchQuery query) {
        var beaconQuery = BeaconGVariantsRequestMapper.map(query);

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
        var requestedCountry = parseCountryFilter(countryOfBirth);
        var requestedSex = parseSexFilter(sex);

        if (requestedCountry.isUnset() && requestedSex.isUnset()) {
            return true;
        }

        if (requestedCountry.isInvalid() || requestedSex.isInvalid()) {
            return false;
        }

        if (requestedCountry.isAll() && requestedSex.isAll()) {
            return true;
        }

        var populationTag = parsePopulationTag(variant.getPopulation());
        if (populationTag == null) {
            return false;
        }

        if (requestedCountry.isAll() && requestedSex.isSpecific()) {
            return populationTag.sex() == requestedSex.sex()
                    && (populationTag.isSexOnly() || populationTag.isCountryAndSex());
        }

        if (requestedCountry.isSpecific() && requestedSex.isAll()) {
            return requestedCountry.country().equals(populationTag.country())
                    && (populationTag.isCountryOnly() || populationTag.isCountryAndSex());
        }

        boolean countryMustBePresent = !requestedCountry.isUnset();
        boolean sexMustBePresent = !requestedSex.isUnset();

        return countryMatches(populationTag, requestedCountry, countryMustBePresent)
                && sexMatches(populationTag, requestedSex, sexMustBePresent);
    }

    private boolean countryMatches(PopulationTag populationTag, CountryFilter requestedCountry,
            boolean countryMustBePresent) {
        if (countryMustBePresent != (populationTag.country() != null)) {
            return false;
        }
        return !requestedCountry.isSpecific() || requestedCountry.country().equals(populationTag
                .country());
    }

    private boolean sexMatches(PopulationTag populationTag, SexFilter requestedSex,
            boolean sexMustBePresent) {
        if (sexMustBePresent != (populationTag.sex() != null)) {
            return false;
        }
        return !requestedSex.isSpecific() || requestedSex.sex() == populationTag.sex();
    }

    private PopulationTag parsePopulationTag(String population) {
        if (!hasValue(population)) {
            return null;
        }

        var rawTokens = population.trim().split("_");
        if (rawTokens.length == 1) {
            var token = normalizeToken(rawTokens[0]);
            var sex = normalizeSex(token);
            if (sex != null) {
                return new PopulationTag(null, sex);
            }

            var country = normalizeCountry(token);
            if (country != null) {
                return new PopulationTag(country, null);
            }

            return null;
        }

        if (rawTokens.length == 2) {
            var country = normalizeCountry(rawTokens[0]);
            var sex = normalizeSex(rawTokens[1]);
            if (country != null && sex != null) {
                return new PopulationTag(country, sex);
            }
        }

        return null;
    }

    private String normalizeCountry(String value) {
        var normalized = normalizeToken(value);
        return normalized != null && COUNTRY_CODE_PATTERN.matcher(normalized).matches()
                ? normalized
                : null;
    }

    private Sex normalizeSex(String value) {
        var normalized = normalizeToken(value);
        if (normalized == null) {
            return null;
        }

        return switch (normalized) {
            case "M", "MALE" -> Sex.MALE;
            case "F", "FEMALE" -> Sex.FEMALE;
            default -> null;
        };
    }

    private String normalizeToken(String value) {
        return hasValue(value) ? value.trim().toUpperCase(Locale.ROOT) : null;
    }

    private CountryFilter parseCountryFilter(String value) {
        var normalized = normalizeToken(value);
        if (normalized == null) {
            return new CountryFilter(FilterMode.UNSET, null);
        }

        if (ALL_FILTER_VALUE.equals(normalized)) {
            return new CountryFilter(FilterMode.ALL, null);
        }

        var country = normalizeCountry(normalized);
        if (country != null) {
            return new CountryFilter(FilterMode.SPECIFIC, country);
        }

        return new CountryFilter(FilterMode.INVALID, null);
    }

    private SexFilter parseSexFilter(String value) {
        var normalized = normalizeToken(value);
        if (normalized == null) {
            return new SexFilter(FilterMode.UNSET, null);
        }

        if (ALL_FILTER_VALUE.equals(normalized)) {
            return new SexFilter(FilterMode.ALL, null);
        }

        var sex = normalizeSex(normalized);
        if (sex != null) {
            return new SexFilter(FilterMode.SPECIFIC, sex);
        }

        return new SexFilter(FilterMode.INVALID, null);
    }

    private enum Sex {
        MALE, FEMALE
    }

    private enum FilterMode {
        UNSET, ALL, SPECIFIC, INVALID
    }

    private record PopulationTag(String country, Sex sex) {

        boolean isCountryOnly() {
            return country != null && sex == null;
        }

        boolean isSexOnly() {
            return country == null && sex != null;
        }

        boolean isCountryAndSex() {
            return country != null && sex != null;
        }
    }

    private record CountryFilter(FilterMode mode, String country) {

        boolean isUnset() {
            return mode == FilterMode.UNSET;
        }

        boolean isAll() {
            return mode == FilterMode.ALL;
        }

        boolean isSpecific() {
            return mode == FilterMode.SPECIFIC;
        }

        boolean isInvalid() {
            return mode == FilterMode.INVALID;
        }
    }

    private record SexFilter(FilterMode mode, Sex sex) {

        boolean isUnset() {
            return mode == FilterMode.UNSET;
        }

        boolean isAll() {
            return mode == FilterMode.ALL;
        }

        boolean isSpecific() {
            return mode == FilterMode.SPECIFIC;
        }

        boolean isInvalid() {
            return mode == FilterMode.INVALID;
        }
    }
}
