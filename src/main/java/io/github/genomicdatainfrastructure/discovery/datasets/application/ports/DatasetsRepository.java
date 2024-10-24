// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.application.ports;

import io.github.genomicdatainfrastructure.discovery.model.RetrievedDataset;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;

import java.util.List;
import java.util.Set;

public interface DatasetsRepository {

    List<SearchedDataset> search(
            Set<String> datasetIds,
            String sort,
            Integer rows,
            Integer start,
            String accessToken);

    RetrievedDataset findById(String id, String accessToken);

    String retrieveDatasetInFormat(String id, String format, String accessToken);
}
