package io.github.genomicdatainfrastructure.discovery.datasets.infra.persistence.ckan;

import io.github.genomicdatainfrastructure.discovery.datasets.applications.ports.FacetsBuilder;
import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import io.github.genomicdatainfrastructure.discovery.utils.CkanFacetsQueryBuilder;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class CkanFacetsBuilder implements FacetsBuilder {
    private final static String CKAN_FACET_GROUP = "ckan";
    private static final String SELECTED_FACETS = "[\"access_rights\",\"theme\",\"tags\",\"spatial_uri\",\"organization\",\"publisher_name\",\"res_format\"]";

    private final CkanQueryApi ckanQueryApi;

    @Inject
    public CkanFacetsBuilder(CkanQueryApi ckanQueryApi) {
        this.ckanQueryApi = ckanQueryApi;
    }

    @Override
    public FacetGroup buildFacets(DatasetSearchQuery query, String accessToken) {
        var facetsQuery = CkanFacetsQueryBuilder.buildFacetQuery(query);

        var response = ckanQueryApi.packageSearch(
                query.getQuery(),
                facetsQuery,
                query.getSort(),
                0,
                null,
                SELECTED_FACETS,
                accessToken
        );

        var nonNullSearchFacets = ofNullable(response.getResult())
                .map(PackagesSearchResult::getSearchFacets)
                .orElseGet(Map::of);

        return facetGroup(nonNullSearchFacets);
    }

    private FacetGroup facetGroup(Map<String, CkanFacet> facets) {
        return FacetGroup.builder()
                .key(CKAN_FACET_GROUP)
                .label("DCAT-AP")
                .facets(facets.entrySet().stream()
                        .map(this::facet)
                        .toList())
                .build();
    }

    private Facet facet(Map.Entry<String, CkanFacet> entry) {
        var key = entry.getKey();
        var facet = entry.getValue();
        var values = ofNullable(facet.getItems())
                .orElseGet(List::of)
                .stream()
                .map(value -> ValueLabel.builder()
                        .value(value.getName())
                        .label(value.getDisplayName())
                        .build()
                )
                .toList();

        return Facet.builder()
                .key(key)
                .label(facet.getTitle())
                .values(values)
                .build();
    }

}
