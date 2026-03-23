// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config;

public class CkanConfiguration {

    public static final String CKAN_IDENTIFIER_FIELD = "identifier";
    public static final String CKAN_FILTER_SOURCE = "ckan";
    public static final int CKAN_PAGINATION_MAX_SIZE = 1000;
    public static final String CKAN_DATASET_TYPE_FILTER = "type:(\"dataset\")";

    public static String withDatasetTypeFilter(String fq) {
        if (fq == null || fq.isBlank()) {
            return CKAN_DATASET_TYPE_FILTER;
        }
        return "%s AND %s".formatted(fq, CKAN_DATASET_TYPE_FILTER);
    }
}
