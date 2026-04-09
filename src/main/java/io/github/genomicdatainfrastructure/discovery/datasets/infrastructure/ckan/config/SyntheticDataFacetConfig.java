// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config;

import lombok.experimental.UtilityClass;

import java.util.Locale;
import java.util.Set;

@UtilityClass
public class SyntheticDataFacetConfig {

    public static final String TYPE_FACET_KEY = "dcat_type";

    // Hardcoded CKAN dct:type values that identify synthetic datasets.
    public static final Set<String> SYNTHETIC_DATASET_TYPES = Set.of(
            "http://publications.europa.eu/resource/authority/dataset-type/SYNTHETIC_DATA"
    );

    public static boolean isSyntheticDatasetType(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        var normalized = normalizeUri(value);
        return SYNTHETIC_DATASET_TYPES.stream()
                .map(SyntheticDataFacetConfig::normalizeUri)
                .anyMatch(normalized::equals);
    }

    private static String normalizeUri(String uri) {
        var trimmed = uri.trim().toLowerCase(Locale.ROOT);
        // Remove protocol (http:// or https://) for protocol-agnostic comparison
        if (trimmed.startsWith("https://")) {
            return trimmed.substring(8);
        }
        if (trimmed.startsWith("http://")) {
            return trimmed.substring(7);
        }
        return trimmed;
    }
}
