package io.github.genomicdatainfrastructure.discovery.datasets.applications.ports;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.FacetGroup;

public interface FacetsBuilder {
    FacetGroup buildFacets(DatasetSearchQuery query, String accessToken);
}
