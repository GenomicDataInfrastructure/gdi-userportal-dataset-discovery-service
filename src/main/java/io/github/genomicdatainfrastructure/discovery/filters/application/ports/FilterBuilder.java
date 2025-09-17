// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.application.ports;

import io.github.genomicdatainfrastructure.discovery.model.Filter;

import java.util.List;

public interface FilterBuilder {

    List<Filter> build(String accessToken, String preferredLanguage);
}
