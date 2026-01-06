// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.application.ports;

import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.GVariantAlleleFrequencyResponse;

public interface GVariantsRepositoryPort {

    GVariantAlleleFrequencyResponse search(GVariantSearchQuery gVariantSearchQuery);

}
