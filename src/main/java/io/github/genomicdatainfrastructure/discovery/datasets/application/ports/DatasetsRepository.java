// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.application.ports;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.RetrievedDataset;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;

import java.util.List;
import java.util.Set;

public interface DatasetsRepository {

    SearchResult search(
            DatasetSearchQuery query,
            String accessToken,
            String preferredLanguage);

    SearchResult search(
            Set<String> datasetIds,
            String sort,
            Integer rows,
            Integer start,
            String accessToken,
            String preferredLanguage);

    RetrievedDataset findById(String id, String accessToken, String preferredLanguage);

    String retrieveDatasetInFormat(String id, String format, String accessToken);

    record SearchResult(int count, List<SearchedDataset> results) {
    }
}
