// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.services;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanContactPoint;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanCreator;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanDatasetDictionaryEntry;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanDatasetRelationEntry;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanOrganization;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackage;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanResource;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanTag;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanValueLabel;
import io.github.genomicdatainfrastructure.discovery.utils.PackageShowMapper;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.LocalDateTime.parse;
import static org.assertj.core.api.Assertions.assertThat;

class PackageShowMapperTest {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    );

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
                .publisherName("publisherName")
                .organization(CkanOrganization.builder()
                        .title("organization")
                        .description("description")
                        .imageUrl("https://image.com")
                        .build())
                .metadataCreated("2024-03-19T13:37:05.472970")
                .metadataModified("2024-03-19T13:37:05.472970")
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
                .contactUri("contactUri")
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
                                .uri("uri")
                                .created("2024-03-19T13:37:05.472970")
                                .lastModified("2024-03-19T13:37:05.472970")
                                .build()
                ))
                .contactPoint(List.of(
                        CkanContactPoint.builder()
                                .contactName("Contact 1")
                                .contactEmail("contact1@example.com")
                                .build(),
                        CkanContactPoint.builder()
                                .contactName("Contact 2")
                                .contactEmail("contact2@example.com")
                                .contactUri("http://example.com")
                                .build()
                ))
                .creator(List.of(
                        CkanCreator.builder()
                                .creatorName("creatorName")
                                .creatorIdentifier("creatorIdentifier")
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
                                .build()
                ))
                .publisherName("publisherName")
                .catalogue("organization")
                .createdAt(parse("2024-03-19T13:37:05.472970", DATE_FORMATTER))
                .modifiedAt(parse("2024-03-19T13:37:05.472970", DATE_FORMATTER))
                .url("url")
                .languages(List.of(
                        ValueLabel.builder()
                                .value("en")
                                .label("language")
                                .build()
                ))
                .contact(ValueLabel.builder()
                        .value("contactUri")
                        .label("contactUri")
                        .build())
                .hasVersions(List.of(
                        ValueLabel.builder()
                                .value("1")
                                .label("version")
                                .build()
                ))
                .creators(List.of(
                        ValueLabel.builder()
                                .label("creatorName")
                                .value("creatorIdentifier")
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
                                .build()
                ))
                .organization(DatasetOrganization
                        .builder()
                        .title("organization")
                        .description("description")
                        .imageUrl("https://image.com")
                        .build())
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
                                .format(ValueLabel.builder()
                                        .value("pdf")
                                        .label("format")
                                        .build())
                                .uri("uri")
                                .createdAt(parse("2024-03-19T13:37:05.472970", DATE_FORMATTER))
                                .modifiedAt(parse("2024-03-19T13:37:05.472970", DATE_FORMATTER))
                                .build()
                ))
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
                        DatasetRelationEntry.builder().relation("Relation 1").target("Dataset 1")
                                .build(),
                        DatasetRelationEntry.builder().relation("Relation 2").target("Dataset 2")
                                .build()
                ))
                .dataDictionary(List.of(
                        DatasetDictionaryEntry.builder().name("Entry 1").type("Type 1").description(
                                "Description 1").build(),
                        DatasetDictionaryEntry.builder().name("Entry 2").type("Type 2").description(
                                "Description 2").build()
                ))
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
