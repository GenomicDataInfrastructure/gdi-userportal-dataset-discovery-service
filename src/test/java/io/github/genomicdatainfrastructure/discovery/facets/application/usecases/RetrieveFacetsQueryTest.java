// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.facets.application.usecases;

import io.github.genomicdatainfrastructure.discovery.facets.application.RetrieveFacetsQuery;
import io.github.genomicdatainfrastructure.discovery.facets.ports.FacetsBuilder;
import io.github.genomicdatainfrastructure.discovery.model.Facet;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import jakarta.enterprise.inject.Instance;
import org.junit.Assert;
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
class RetrieveFacetsQueryTest {

    @Mock
    private Instance<FacetsBuilder> facetBuilders;

    @Mock
    private FacetsBuilder facetBuilderCkan;

    @Mock
    private FacetsBuilder facetBuilderBeacon;

    @InjectMocks
    private RetrieveFacetsQuery query;

    @BeforeEach
    public void setUp() {
        when(facetBuilders.stream()).thenReturn(Stream.of(facetBuilderCkan, facetBuilderBeacon));
    }

    @Test
    void shouldRetrieveAllFacets() {
        var mockCkanFacets = List.of(
                Facet.builder()
                        .key("tags")
                        .label("tags")
                        .facetGroup("ckan")
                        .values(
                                List.of(ValueLabel.builder()
                                        .label("genomics")
                                        .value("5")
                                        .build()))
                        .build());

        when(facetBuilderCkan.build(anyString())).thenReturn(mockCkanFacets);

        var mockBeaconFacets = List.of(
                Facet.builder()
                        .key("Human Phenotype Ontology")
                        .label("ontology")
                        .facetGroup("beacon")
                        .values(
                                List.of(ValueLabel.builder()
                                        .label("Motor delay")
                                        .value("3")
                                        .build()))
                        .build());
        when(facetBuilderBeacon.build(anyString())).thenReturn(mockBeaconFacets);

        var facets = query.execute("token");

        Assert.assertNotNull(facets);
        Assert.assertEquals(2, facets.size());

        Assert.assertEquals("ckan", facets.get(0).getFacetGroup());
        Assert.assertEquals("tags", facets.get(0).getKey());
        Assert.assertEquals("tags", facets.get(0).getLabel());
        Assert.assertEquals("genomics", facets.get(0).getValues().get(0).getLabel());
        Assert.assertEquals("5", facets.get(0).getValues().get(0).getValue());

        Assert.assertEquals("beacon", facets.get(1).getFacetGroup());
        Assert.assertEquals("Human Phenotype Ontology", facets.get(1).getKey());
        Assert.assertEquals("ontology", facets.get(1).getLabel());
        Assert.assertEquals("Motor delay", facets.get(1).getValues().get(0).getLabel());
        Assert.assertEquals("3", facets.get(1).getValues().get(0).getValue());
    }

    @Test
    void shouldRetrieveFacetsFromOneSourceWhenTheOtherIsNull() {
        var mockCkanFacets = List.of(
                Facet.builder()
                        .key("tags")
                        .label("tags")
                        .facetGroup("ckan")
                        .values(
                                List.of(ValueLabel.builder()
                                        .label("genomics")
                                        .value("5")
                                        .build()))
                        .build());

        when(facetBuilderCkan.build(anyString())).thenReturn(mockCkanFacets);

        when(facetBuilderBeacon.build(anyString())).thenReturn(null);

        var facets = query.execute("token");

        Assert.assertNotNull(facets);
        Assert.assertEquals(1, facets.size());

        Assert.assertEquals("ckan", facets.get(0).getFacetGroup());
        Assert.assertEquals("tags", facets.get(0).getKey());
        Assert.assertEquals("tags", facets.get(0).getLabel());
        Assert.assertEquals("genomics", facets.get(0).getValues().get(0).getLabel());
        Assert.assertEquals("5", facets.get(0).getValues().get(0).getValue());
    }
}
