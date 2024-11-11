// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.discovery.filters.application.ports;

import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;

import java.util.List;

public interface FiltersRepository {

    List<ValueLabel> getValuesForFilter(String key);

}
