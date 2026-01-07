// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.config.GoEPopulationPatternConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class GoEPopulationPatternExtractor {

    private final GoEPopulationPatternConfig config;

    public String extractPopulation(String datasetName) {
        if (datasetName == null || datasetName.isEmpty()) {
            return null;
        }

        String[] parts = datasetName.split(Pattern.quote(config.separator()), -1);
        String country = extractCountry(parts);
        String sex = extractSex(parts);
        return buildPopulationString(country, sex);
    }

    private String extractCountry(String[] parts) {
        int position = config.countryPosition();
        if (position < 0 || position >= parts.length) {
            return null;
        }
        String candidate = parts[position].trim();
        return candidate.isEmpty() ? null : candidate.toUpperCase();
    }

    private String extractSex(String[] parts) {
        int position = config.sexPosition();
        if (position < 0 || position >= parts.length) {
            return null;
        }
        String candidate = parts[position].trim();
        return isSexCode(candidate) ? candidate.toUpperCase() : null;
    }

    private boolean isSexCode(String value) {
        if (value == null || value.length() != 1) {
            return false;
        }
        String upper = value.toUpperCase();
        return "M".equals(upper) || "F".equals(upper);
    }

    private String buildPopulationString(String country, String sex) {
        if (country == null && sex == null) {
            return null;
        }
        if (country != null && sex != null) {
            return country + config.separator() + sex;
        }
        return country != null ? country : sex;
    }
}