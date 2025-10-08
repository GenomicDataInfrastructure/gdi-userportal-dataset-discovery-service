// SPDX-FileCopyrightText: 2025 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.quarkus;

import io.github.genomicdatainfrastructure.discovery.model.FilterType;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import java.util.List;
import java.util.Set;

@ConfigMapping(prefix = "datasets")
public interface DatasetsConfig {

    String filters();

    @WithDefault("NO_GROUP")
    String noGroupKey();

    List<FilterGroup> filterGroups();

    interface FilterGroup {

        String key();

        Set<Filter> filters();
    }

    interface Filter {

        String key();

        @WithDefault("DROPDOWN")
        FilterType type();
    }
}
