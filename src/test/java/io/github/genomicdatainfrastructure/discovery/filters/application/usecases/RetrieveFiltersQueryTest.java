// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.application.usecases;

import io.github.genomicdatainfrastructure.discovery.filters.application.ports.FilterBuilder;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.model.FilterType;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import jakarta.enterprise.inject.Instance;
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

    @InjectMocks
    private RetrieveFiltersQuery query;

    @BeforeEach
    public void setUp() {
        when(filterBuilders.stream()).thenReturn(Stream.of(filterBuilderCkan, filterBuilderBeacon));
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

        when(filterBuilderCkan.build(anyString())).thenReturn(mockCkanFilters);

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
        when(filterBuilderBeacon.build(anyString())).thenReturn(mockBeaconFilters);

        var filters = query.execute("token");

        Assertions.assertThat(filters).isNotNull();
        Assertions.assertThat(filters).hasSize(2);

        Assertions.assertThat(filters.get(0))
                .extracting("source", "type", "key", "label")
                .containsExactly("ckan", FilterType.DROPDOWN, "tags", "tags");

        Assertions.assertThat(filters.get(0).getValues().get(0))
                .extracting("label", "value")
                .containsExactly("genomics", "5");

        Assertions.assertThat(filters.get(1))
                .extracting("source", "type", "key", "label")
                .containsExactly("beacon", FilterType.DROPDOWN, "Human Phenotype Ontology",
                        "ontology");

        Assertions.assertThat(filters.get(1).getValues().get(0))
                .extracting("label", "value")
                .containsExactly("Motor delay", "3");
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

        when(filterBuilderCkan.build(anyString())).thenReturn(mockCkanFilters);

        when(filterBuilderBeacon.build(anyString())).thenReturn(null);

        var filters = query.execute("token");

        Assertions.assertThat(filters).isNotNull();
        Assertions.assertThat(filters).hasSize(1);

        Assertions.assertThat(filters.get(0))
                .extracting("source", "type", "key", "label")
                .containsExactly("ckan", FilterType.DROPDOWN, "tags", "tags");

        Assertions.assertThat(filters.get(0).getValues().get(0))
                .extracting("label", "value")
                .containsExactly("genomics", "5");
    }

    @Test
    void shouldHandleExceptionFromFilterBuilder() {
        when(filterBuilderCkan.build(anyString())).thenThrow(new RuntimeException(
                "Test exception"));
        when(filterBuilderBeacon.build(anyString())).thenReturn(List.of(
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
                        .build())
        );

        var filters = query.execute("token");

        Assertions.assertThat(filters).isNotNull();
        Assertions.assertThat(filters).hasSize(1);

        Assertions.assertThat(filters.get(0).getSource()).isEqualTo("beacon");
    }
}
