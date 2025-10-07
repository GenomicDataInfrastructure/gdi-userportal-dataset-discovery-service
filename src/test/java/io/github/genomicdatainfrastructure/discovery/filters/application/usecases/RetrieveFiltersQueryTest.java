// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.application.usecases;

import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FilterBuilder;
import io.github.genomicdatainfrastructure.discovery.filters.infrastructure.quarkus.DatasetsConfig;
import io.github.genomicdatainfrastructure.discovery.filters.infrastructure.quarkus.DatasetsConfig.FilterGroup;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.model.FilterType;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import jakarta.enterprise.inject.Instance;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetrieveFiltersQueryTest {

    @Mock
    private Instance<FilterBuilder> filterBuilders;

    @Mock
    private FilterBuilder filterBuilderCkan;

    @Mock
    private FilterBuilder filterBuilderBeacon;

    @Mock
    private DatasetsConfig datasetsConfig;

    @InjectMocks
    private RetrieveFiltersQuery query;

    @BeforeEach
    public void setUp() {
        when(filterBuilders.stream()).thenReturn(Stream.of(filterBuilderCkan, filterBuilderBeacon));
        when(datasetsConfig.noGroupKey()).thenReturn("NO_GROUP");
    }

    @Test
    void shouldRetrieveAllFilters() {
        var mockCkanFilters = List.of(
                Filter.builder()
                        .source("ckan")
                        .type(FilterType.DROPDOWN)
                        .key("tags")
                        .label("tags")
                        .values(
                                List.of(ValueLabel.builder()
                                        .label("genomics")
                                        .value("5")
                                        .build()))
                        .build());

        when(filterBuilderCkan.build(anyString(), any())).thenReturn(mockCkanFilters);

        var mockBeaconFilters = List.of(
                Filter.builder()
                        .source("beacon")
                        .type(FilterType.DROPDOWN)
                        .key("Human Phenotype Ontology")
                        .label("ontology")
                        .values(
                                List.of(ValueLabel.builder()
                                        .label("Motor delay")
                                        .value("3")
                                        .build()))
                        .build());
        when(filterBuilderBeacon.build(anyString(), any())).thenReturn(mockBeaconFilters);

        var actual = query.execute("token", "en");

        Assertions.assertThat(actual)
                .containsExactlyInAnyOrder(Filter.builder()
                        .source("beacon")
                        .group("NO_GROUP")
                        .type(FilterType.DROPDOWN)
                        .key("Human Phenotype Ontology")
                        .label("ontology")
                        .values(
                                List.of(ValueLabel.builder()
                                        .label("Motor delay")
                                        .value("3")
                                        .build()))
                        .build(),
                        Filter.builder()
                                .source("ckan")
                                .group("NO_GROUP")
                                .type(FilterType.DROPDOWN)
                                .key("tags")
                                .label("tags")
                                .values(
                                        List.of(ValueLabel.builder()
                                                .label("genomics")
                                                .value("5")
                                                .build()))
                                .build()
                );
    }

    @Test
    void shouldRetrieveFiltersFromOneSource_WhenTheOtherIsNull() {
        var mockCkanFilters = List.of(
                Filter.builder()
                        .key("tags")
                        .type(FilterType.DROPDOWN)
                        .label("tags")
                        .source("ckan")
                        .values(
                                List.of(ValueLabel.builder()
                                        .label("genomics")
                                        .value("5")
                                        .build()))
                        .build());

        when(filterBuilderCkan.build(anyString(), any())).thenReturn(mockCkanFilters);

        when(filterBuilderBeacon.build(anyString(), any())).thenReturn(null);

        var actual = query.execute("token", null);

        Assertions.assertThat(actual)
                .containsExactly(Filter.builder()
                        .group("NO_GROUP")
                        .key("tags")
                        .type(FilterType.DROPDOWN)
                        .label("tags")
                        .source("ckan")
                        .values(
                                List.of(ValueLabel.builder()
                                        .label("genomics")
                                        .value("5")
                                        .build()))
                        .build());
    }

    @Test
    void shouldGroupFilters() {
        when(datasetsConfig.filterGroups()).thenReturn(List.of(
                new MockFilterGroup("CKAN_GROUP", Set.of(new MockFilter("tags", false))),
                new MockFilterGroup("BEACON_GROUP",
                        Set.of(new MockFilter("Human Phenotype Ontology", false)))
        ));
        when(datasetsConfig.noGroupKey()).thenReturn("DUMMY");

        var mockCkanFilters = List.of(
                Filter.builder()
                        .source("ckan")
                        .type(FilterType.DROPDOWN)
                        .key("tags")
                        .label("tags")
                        .values(
                                List.of(ValueLabel.builder()
                                        .label("genomics")
                                        .value("5")
                                        .build()))
                        .build());

        when(filterBuilderCkan.build(anyString(), any())).thenReturn(mockCkanFilters);

        var mockBeaconFilters = List.of(
                Filter.builder()
                        .source("beacon")
                        .type(FilterType.DROPDOWN)
                        .key("Human Phenotype Ontology")
                        .label("ontology")
                        .values(
                                List.of(ValueLabel.builder()
                                        .label("Motor delay")
                                        .value("3")
                                        .build()))
                        .build());
        when(filterBuilderBeacon.build(anyString(), any())).thenReturn(mockBeaconFilters);

        var actual = query.execute("token", "en");

        Assertions.assertThat(actual)
                .containsExactlyInAnyOrder(Filter.builder()
                        .source("beacon")
                        .group("BEACON_GROUP")
                        .type(FilterType.DROPDOWN)
                        .key("Human Phenotype Ontology")
                        .label("ontology")
                        .values(
                                List.of(ValueLabel.builder()
                                        .label("Motor delay")
                                        .value("3")
                                        .build()))
                        .build(),
                        Filter.builder()
                                .source("ckan")
                                .group("CKAN_GROUP")
                                .type(FilterType.DROPDOWN)
                                .key("tags")
                                .label("tags")
                                .values(
                                        List.of(ValueLabel.builder()
                                                .label("genomics")
                                                .value("5")
                                                .build()))
                                .build()
                );
    }

    record MockFilterGroup(String key, Set<DatasetsConfig.Filter> filters) implements FilterGroup {
    }

    record MockFilter(String key, Boolean isDateTime, Boolean isNumber) implements DatasetsConfig.Filter {
    }
}
