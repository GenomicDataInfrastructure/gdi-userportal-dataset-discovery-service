// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "goe.population-pattern")
public interface GoEPopulationPatternConfig {

    @WithDefault("1")
    int countryPosition();

    @WithDefault("2")
    int sexPosition();

    @WithDefault("_")
    String separator();
}
