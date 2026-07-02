// SPDX-FileCopyrightText: 2025 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.quarkus;

import io.github.genomicdatainfrastructure.discovery.model.FilterType;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ConfigMapping(prefix = "datasets")
public interface DatasetsConfig {

    String filters();

    @WithDefault("NO_GROUP")
    String noGroupKey();

    /**
     * Whether to request Solr stats (min/max) for filters configured with a statsField.
     * Off by default since it requires CKAN's enhanced_package_search action to support the
     * stats/stats.field params, which not every CKAN deployment has yet.
     */
    @WithDefault("false")
    boolean statsEnabled();

    List<FilterGroup> filterGroups();

    interface FilterGroup {

        String key();

        Set<Filter> filters();
    }

    interface Filter {

        String key();

        @WithDefault("DROPDOWN")
        FilterType type();

        Optional<List<String>> rangeComposite();

        /**
         * The underlying Solr field to request stats (min/max) for, for filters whose range
         * can't be derived from facet items (e.g. temporal_coverage, backed by the
         * temporal_coverage_range DateRangeField).
         */
        Optional<String> statsField();
    }
}
