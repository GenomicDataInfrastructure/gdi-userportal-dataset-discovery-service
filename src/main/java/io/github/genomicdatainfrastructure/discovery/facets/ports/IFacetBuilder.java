package io.github.genomicdatainfrastructure.discovery.facets.ports;

import io.github.genomicdatainfrastructure.discovery.model.Facet;

import java.util.List;

public interface IFacetBuilder {

    List<Facet> build(String accessToken);
}
