// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.mapper;

import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFacet;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.mapstruct.CollectionMappingStrategy.ADDER_PREFERRED;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

@Mapper(componentModel = "jakarta", nullValueCheckStrategy = ALWAYS, nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, collectionMappingStrategy = ADDER_PREFERRED)
public interface CkanFilterMapper {

    default List<ValueLabel> map(final PackagesSearchResponse packagesSearchResponse,
            final String key) {
        if (Objects.isNull(packagesSearchResponse) || Objects.isNull(packagesSearchResponse
                .getResult())) {
            return Collections.emptyList();
        }
        return map(packagesSearchResponse.getResult().getSearchFacets(), key);
    }

    default List<ValueLabel> map(final Map<String, CkanFacet> ckanFacets, final String key) {
        if (Objects.isNull(ckanFacets)) {
            return Collections.emptyList();
        }
        return map(ckanFacets.get(key));
    }

    default List<ValueLabel> map(final CkanFacet ckanFacet) {
        if (Objects.isNull(ckanFacet)) {
            return Collections.emptyList();
        }
        return map(ckanFacet.getItems());
    }

    List<ValueLabel> map(final List<CkanValueLabel> items);

    @Mapping(target = "value", source = "name")
    @Mapping(target = "label", source = "displayName")
    @Mapping(target = "count", source = "count")
    ValueLabel map(final CkanValueLabel valueLabel);
}
