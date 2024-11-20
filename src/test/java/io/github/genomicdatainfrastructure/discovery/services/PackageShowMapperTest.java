// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils.PackageShowMapper;
import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.time.OffsetDateTime.parse;
import static org.assertj.core.api.Assertions.assertThat;

class PackageShowMapperTest {

    @Test
    void accepts_empty_package() {
        var ckanPackage = CkanPackage.builder().build();

        var actual = PackageShowMapper.from(ckanPackage);
        var expected = RetrievedDataset.builder()
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
    void can_parse() {
        var ckanPackage = CkanPackage.builder()
                .id("id")
                .identifier("identifier")
                .title("title")
                .notes("notes")
                .theme(List.of(CkanValueLabel.builder()
                        .displayName("theme")
                        .name("theme-name")
                        .build()))
                .issued("2024-07-01T22:00:00+00:00")
                .modified("2024-07-02T22:00:00Z")
                .tags(List.of(CkanTag.builder()
                        .displayName("key-tag")
                        .id("tag-id")
                        .name("key")
                        .build()))
                .url("url")
                .language(List.of(
                        CkanValueLabel.builder()
                                .displayName("language")
                                .name("en")
                                .build()))
                .hasVersion(List.of(
                        CkanValueLabel.builder()
                                .displayName("version")
                                .name("1")
                                .build()))
                .accessRights(CkanValueLabel.builder()
                        .displayName("accessRights")
                        .name("public")
                        .build())
                .conformsTo(List.of(
                        CkanValueLabel.builder()
                                .displayName("conformsTo")
                                .name("conforms")
                                .build()))
                .provenance("provenance")
                .spatialUri(CkanValueLabel.builder()
                        .displayName("spatial")
                        .name("uri")
                        .build())
                .resources(List.of(
                        CkanResource.builder()
                                .id("resource_id")
                                .name("resource_name")
                                .description("resource_description")
                                .format(CkanValueLabel.builder()
                                        .displayName("format")
                                        .name("pdf")
                                        .build())
                                .accessUrl("accessUrl")
                                .downloadUrl("downloadUrl")
                                .issuedDate("2025-03-19")
                                .modifiedDate("2025-03-19T13:37:05Z")
                                .language(List.of(
                                        CkanValueLabel.builder()
                                                .displayName("language")
                                                .name("en")
                                                .build()))
                                .build()))
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

        var actual = PackageShowMapper.from(ckanPackage);
        var expected = RetrievedDataset.builder()
                .id("id")
                .identifier("identifier")
                .title("title")
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
                                .type("type")
                                .uri("uri")
                                .build(),
                        Agent.builder()
                                .name("creatorName2")
                                .identifier("creatorIdentifier2")
                                .email("email2")
                                .url("url2")
                                .type("type2")
                                .uri("uri2")
                                .build()
                ))
                .publishers(List.of(
                        Agent.builder()
                                .name("publisherName")
                                .identifier("publisherIdentifier")
                                .email("email")
                                .url("url")
                                .type("type")
                                .uri("uri")
                                .build(),
                        Agent.builder()
                                .name("publisherName2")
                                .identifier("publisherIdentifier2")
                                .email("email2")
                                .url("url2")
                                .type("type2")
                                .uri("uri2")
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
                                .identifier("contact-identifier-1")
                                .build(),
                        ContactPoint.builder()
                                .name("Contact 2")
                                .email("contact2@example.com")
                                .uri("http://example.com")
                                .identifier("contact-identifier-2")
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
