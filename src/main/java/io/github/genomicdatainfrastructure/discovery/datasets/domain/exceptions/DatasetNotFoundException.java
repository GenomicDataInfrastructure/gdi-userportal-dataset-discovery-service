// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.domain.exceptions;

public class DatasetNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Dataset %s not found";

    public DatasetNotFoundException(String datasetId) {
        super(MESSAGE.formatted(datasetId));
    }
}
