// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.mapper;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.time.OffsetDateTime.parse;
import static org.assertj.core.api.Assertions.assertThat;

class CkanDatasetsMapperTest {

    private final CkanDatasetsMapper mapper = new CkanDatasetsMapperImpl();

    @Nested
    class RetrievedDatasetTest {

        @Test
        void given_ckanPackage_with_empty_collections_should_be_mapped_to_empty_Lists() {
            final var ckanPackage = CkanPackage.builder().build();

            final var actual = mapper.map(ckanPackage);
            final var expected = RetrievedDataset.builder()
                    .conformsTo(List.of())
                    .distributions(List.of())
                    .hasVersions(List.of())
                    .languages(List.of())
                    .themes(List.of())
                    .keywords(List.of())
                    .contacts(List.of())
                    .creators(List.of())
                    .publishers(List.of())
                    .datasetRelationships(List.of())
                    .dataDictionary(List.of())
                    .build();

            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
        }

        @Test
        void given_ckanPackage_should_be_mapped_to_expected_values() {
            final var ckanPackage = buildCkanPackage();

            final var actual = mapper.map(ckanPackage);
            final var expected = RetrievedDataset.builder()
                    .id("id")
                    .identifier("identifier")
                    .title("title")
                    .dcatType(ValueLabel.builder()
                            .value("type-uri")
                            .label("type")
                            .build())
                    .description("notes")
                    .themes(List.of(
                            ValueLabel.builder()
                                    .value("theme-name")
                                    .label("theme")
                                    .build()))
                    .createdAt(parse("2024-07-01T22:00:00+00:00"))
                    .modifiedAt(parse("2024-07-02T22:00:00+00:00"))
                    .url("url")
                    .languages(List.of(
                            ValueLabel.builder()
                                    .value("en")
                                    .label("language")
                                    .build()))
                    .hasVersions(List.of(
                            ValueLabel.builder()
                                    .value("1")
                                    .label("version")
                                    .build()))
                    .creators(List.of(
                            Agent.builder()
                                    .name("creatorName")
                                    .identifier("creatorIdentifier")
                                    .email("email")
                                    .url("url")
                                    .uri("uri")
                                    .type("type")
                                    .build(),
                            Agent.builder()
                                    .name("creatorName2")
                                    .identifier("creatorIdentifier2")
                                    .email("email2")
                                    .url("url2")
                                    .uri("uri2")
                                    .type("type2")
                                    .build()
                    ))
                    .publishers(List.of(
                            Agent.builder()
                                    .name("publisherName")
                                    .identifier("publisherIdentifier")
                                    .email("email")
                                    .url("url")
                                    .uri("uri")
                                    .type("type")
                                    .build(),
                            Agent.builder()
                                    .name("publisherName2")
                                    .identifier("publisherIdentifier2")
                                    .email("email2")
                                    .url("url2")
                                    .uri("uri2")
                                    .type("type2")
                                    .build()
                    ))
                    .accessRights(ValueLabel.builder()
                            .value("public")
                            .label("accessRights")
                            .build())
                    .conformsTo(List.of(
                            ValueLabel.builder()
                                    .value("conforms")
                                    .label("conformsTo")
                                    .build()))
                    .provenance("provenance")
                    .keywords(List.of(ValueLabel.builder()
                            .label("key-tag")
                            .value("key")
                            .build()))
                    .spatial(ValueLabel.builder()
                            .value("uri")
                            .label("spatial")
                            .build())
                    .distributions(List.of(
                            RetrievedDistribution.builder()
                                    .id("resource_id")
                                    .title("resource_name")
                                    .description("resource_description")
                                    .createdAt(parse("2025-03-19T00:00Z"))
                                    .modifiedAt(parse("2025-03-19T13:37:05Z"))
                                    .format(ValueLabel.builder()
                                            .value("pdf")
                                            .label("format")
                                            .build())
                                    .accessUrl("accessUrl")
                                    .downloadUrl("downloadUrl")
                                    .languages(List.of(
                                            ValueLabel.builder()
                                                    .value("en")
                                                    .label("language")
                                                    .build()))
                                    .build()))
                    .contacts(List.of(
                            ContactPoint.builder()
                                    .name("Contact 1")
                                    .email("contact1@example.com")
                                    .build(),
                            ContactPoint.builder()
                                    .name("Contact 2")
                                    .email("contact2@example.com")
                                    .uri("http://example.com")
                                    .build()
                    ))
                    .datasetRelationships(List.of(
                            DatasetRelationEntry.builder().relation("Relation 1")
                                    .target("Dataset 1")
                                    .build(),
                            DatasetRelationEntry.builder().relation("Relation 2")
                                    .target("Dataset 2")
                                    .build()
                    ))
                    .dataDictionary(List.of(
                            DatasetDictionaryEntry.builder().name("Entry 1").type("Type 1")
                                    .description(
                                            "Description 1")
                                    .build(),
                            DatasetDictionaryEntry.builder().name("Entry 2").type("Type 2")
                                    .description(
                                            "Description 2")
                                    .build()
                    ))
                    .build();

            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
        }
    }

    private static CkanPackage buildCkanPackage() {
        return CkanPackage.builder()
                .id("id")
                .identifier("identifier")
                .title("title")
                .dcatType(getCkanValueLabel("type", "type-uri"))
                .notes("notes")
                .theme(getValueLabels("theme", "theme-name"))
                .issued("2024-07-01T22:00:00+00:00")
                .modified("2024-07-02T22:00:00Z")
                .tags(getCkanTags())
                .url("url")
                .language(getValueLabels("language", "en"))
                .hasVersion(getValueLabels("version", "1"))
                .accessRights(getCkanValueLabel("accessRights", "public"))
                .conformsTo(getValueLabels("conformsTo", "conforms"))
                .provenance("provenance")
                .spatialUri(getCkanValueLabel("spatial", "uri"))
                .resources(getCkanResources())
                .contact(List.of(
                        CkanContactPoint.builder()
                                .name("Contact 1")
                                .email("contact1@example.com")
                                .identifier("contact-identifier-1")
                                .build(),
                        CkanContactPoint.builder()
                                .name("Contact 2")
                                .email("contact2@example.com")
                                .uri("http://example.com")
                                .identifier("contact-identifier-2")
                                .build()
                ))
                .creator(List.of(
                        CkanAgent.builder()
                                .name("creatorName")
                                .identifier("creatorIdentifier")
                                .email("email")
                                .url("url")
                                .type("type")
                                .uri("uri")
                                .build(),
                        CkanAgent.builder()
                                .name("creatorName2")
                                .identifier("creatorIdentifier2")
                                .email("email2")
                                .url("url2")
                                .type("type2")
                                .uri("uri2")
                                .build()
                ))
                .publisher(List.of(
                        CkanAgent.builder()
                                .name("publisherName")
                                .identifier("publisherIdentifier")
                                .email("email")
                                .url("url")
                                .type("type")
                                .uri("uri")
                                .build(),
                        CkanAgent.builder()
                                .name("publisherName2")
                                .identifier("publisherIdentifier2")
                                .email("email2")
                                .url("url2")
                                .type("type2")
                                .uri("uri2")
                                .build()
                ))
                .datasetRelationships(List.of(
                        CkanDatasetRelationEntry.builder().target("Dataset 1").relation(
                                "Relation 1").build(),
                        CkanDatasetRelationEntry.builder().target("Dataset 2").relation(
                                "Relation 2").build()
                ))
                .dataDictionary(List.of(
                        CkanDatasetDictionaryEntry.builder().name("Entry 1").type("Type 1")
                                .description("Description 1").build(),
                        CkanDatasetDictionaryEntry.builder().name("Entry 2").type("Type 2")
                                .description("Description 2").build()
                ))
                .build();
    }

    @Nested
    class SearchedDatasetTest {

        static Stream<Arguments> arguments() {
            return Stream.of(
                    Arguments.of(null, Collections.emptyList()), // null strings should be considered blank
                    Arguments.of(PackagesSearchResult.builder().results(Collections.emptyList())
                            .build(), Collections.emptyList()),
                    Arguments.of(PackagesSearchResult.builder().results(List.of(buildCkanPackage()))
                            .build(), List.of(buildSearchedDataset()))
            );
        }

        @ParameterizedTest
        @MethodSource("arguments")
        void given_list_of_SearchedDataset_it_should_be_mapped_properly(PackagesSearchResult result,
                List<SearchedDataset> expected) {
            final var actual = mapper.map(result);
            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
        }

        @NotNull
        private static SearchedDataset buildSearchedDataset() {
            return SearchedDataset.builder()
                    .id("id")
                    .identifier("identifier")
                    .title("title")
                    .description("notes")
                    .publishers(List.of(Agent.builder()
                            .name("publisherName")
                            .email("email")
                            .url("url")
                            .uri("uri")
                            .identifier("publisherIdentifier")
                            .type("type")
                            .build(),
                            Agent.builder()
                                    .name("publisherName2")
                                    .email("email2")
                                    .url("url2")
                                    .uri("uri2")
                                    .identifier("publisherIdentifier2")
                                    .type("type2")
                                    .build()))
                    .themes(List.of(ValueLabel.builder()
                            .value("theme-name")
                            .label("theme")
                            .build()))
                    .keywords(List.of(ValueLabel.builder()
                            .value("key")
                            .label("key-tag")
                            .build()))
                    .modifiedAt(OffsetDateTime.parse("2024-07-02T22:00Z"))
                    .createdAt(OffsetDateTime.parse("2024-07-01T22:00Z"))
                    .distributionsCount(1)
                    .build();
        }
    }

    private static @NotNull List<CkanResource> getCkanResources() {
        return List.of(
                CkanResource.builder()
                        .id("resource_id")
                        .name("resource_name")
                        .description("resource_description")
                        .format(getCkanValueLabel("format", "pdf"))
                        .accessUrl("accessUrl")
                        .downloadUrl("downloadUrl")
                        .issuedDate("2025-03-19")
                        .modifiedDate("2025-03-19T13:37:05Z")
                        .language(getValueLabels("language", "en"))
                        .build());
    }

    private static CkanValueLabel getCkanValueLabel(String spatial, String uri) {
        return CkanValueLabel.builder()
                .displayName(spatial)
                .name(uri)
                .build();
    }

    private static @NotNull List<CkanTag> getCkanTags() {
        return List.of(CkanTag.builder()
                .displayName("key-tag")
                .id("tag-id")
                .name("key")
                .build());
    }

    private static @NotNull List<CkanValueLabel> getValueLabels(String theme, String name) {
        return List.of(getCkanValueLabel(theme, name));
    }
}
