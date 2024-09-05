// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.facets.ports;

import io.github.genomicdatainfrastructure.discovery.model.Facet;

import java.util.List;

public interface IFacetBuilder {

    List<Facet> build(String accessToken);
}
