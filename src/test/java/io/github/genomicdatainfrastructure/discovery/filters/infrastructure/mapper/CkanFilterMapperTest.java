// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.mapper;

import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFacet;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CkanFilterMapperTest {

    private final CkanFilterMapper mapper = new CkanFilterMapperImpl();

    static Stream<PackagesSearchResponse> edgeCases() {
        return Stream.of(
                null,
                new PackagesSearchResponse().result(null),
                new PackagesSearchResponse().result(new PackagesSearchResult().searchFacets(null)),
                new PackagesSearchResponse().result(new PackagesSearchResult().searchFacets(Map.of(
                        "", new CkanFacet()))));
    }

    @ParameterizedTest
    @MethodSource("edgeCases")
    void given_PackagesSearchResponse_but_with_null_objects_should_return_emptyList(
            PackagesSearchResponse input) {
        final var actual = mapper.map(input, "randomKey");
        assertThat(actual).isEmpty();
    }

    @Test
    void given_PackagesSearchResponse_with_valid_input_should_return_expected_ListOfValueLabel() {
        final var input = new PackagesSearchResponse()
                .result(new PackagesSearchResult().searchFacets(
                        Map.of("requestedKey", new CkanFacet()
                                .items(List.of(new CkanValueLabel()
                                        .name("CkanValueName1")
                                        .displayName("CkanValueDisplayName1")
                                        .count(1),
                                        new CkanValueLabel()
                                                .name("CkanValueName2")
                                                .displayName("CkanValueDisplayName2"),
                                        new CkanValueLabel()
                                                .displayName("CkanValueDisplayName3")
                                                .count(3))),
                                "notRequestedKey", new CkanFacet())
                ));
        final var expected = List.of(new ValueLabel()
                .value("CkanValueName1")
                .label("CkanValueDisplayName1")
                .count(1),
                new ValueLabel()
                        .value("CkanValueName2")
                        .label("CkanValueDisplayName2"),
                new ValueLabel()
                        .label("CkanValueDisplayName3")
                        .count(3)
        );

        final var actual = mapper.map(input, "requestedKey");

        assertThat(actual).isNotEmpty();
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
