// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.beacon.strategies;

import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.model.BeaconFilteringTermsResponseContent;

import java.util.List;

public interface BeaconFilterStrategy {

    List<Filter> buildFilters(BeaconFilteringTermsResponseContent content);
}
