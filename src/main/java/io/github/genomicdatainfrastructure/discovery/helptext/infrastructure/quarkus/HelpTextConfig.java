// SPDX-FileCopyrightText: 2026 Health-RI
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.helptext.infrastructure.quarkus;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.time.Duration;
import java.util.Optional;

@ConfigMapping(prefix = "helptext")
public interface HelpTextConfig {

    /**
     * Local file path or http(s) URL to a help-text YAML file for the filters endpoint. When
     * absent, help text is retrieved from CKAN as before.
     */
    Optional<String> filtersSource();

    /**
     * Local file path or http(s) URL to a help-text YAML file for the dataset detail endpoint.
     * When absent, help text is retrieved from CKAN as before.
     */
    Optional<String> datasetSource();

    /**
     * How long a fetched/parsed help-text YAML file is cached in memory before being refreshed.
     */
    @WithDefault("PT5M")
    Duration cacheTtl();
}
