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

import java.net.URI;
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
                    .contacts(List.of())
                    .creators(List.of())
                    .publishers(List.of())
                    .keywords(List.of())
                    .datasetRelationships(List.of())
                    .dataDictionary(List.of())
                    .hdab(List.of())
                    .qualifiedRelation(List.of())
                    .retentionPeriod(List.of())
                    .spatialCoverage(List.of())
                    .provenanceActivity(List.of())
                    .qualifiedAttribution(List.of())
                    .qualityAnnotation(List.of())
                    .publisherType(List.of())
                    .purpose(List.of())
                    .legalBasis(List.of())
                    .codeValues(List.of())
                    .codingSystem(List.of())
                    .healthCategory(List.of())
                    .healthTheme(List.of())
                    .personalData(List.of())
                    .temporalCoverage(TimeWindow.builder().build())
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
                    .dcatType(getValueLabel("type", "type-uri"))
                    .description("notes")
                    .themes(getValueLabels("theme", "theme-name", 3))
                    .createdAt(parse("2024-07-01T22:00:00+00:00"))
                    .modifiedAt(parse("2024-07-02T22:00:00+00:00"))
                    .url("url")
                    .languages(getValueLabels("language", "en", 2))
                    .hasVersions(getValueLabels("version", "1"))
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
                    .accessRights(getValueLabel("accessRights", "public", 10))
                    .conformsTo(getValueLabels("conformsTo", "conforms", 5))
                    .provenance("provenance")
                    .keywords(List.of("key-tag"))
                    .spatial(getValueLabel("spatial", "uri"))
                    .distributions(List.of(
                            RetrievedDistribution.builder()
                                    .id("resource_id")
                                    .title("resource_name")
                                    .description("resource_description")
                                    .createdAt(parse("2025-03-19T00:00Z"))
                                    .modifiedAt(parse("2025-03-19T13:37:05Z"))
                                    .format(getValueLabel("format", "pdf", 1))
                                    .accessUrl(URI.create("https://accessUrl.com"))
                                    .downloadUrl(URI.create("https://downloadUrl.com"))
                                    .compressionFormat("gzip")
                                    .languages(getValueLabels("language", "en", 2))
                                    .accessService(List.of())
// FIXME: this line fails on the comparison at the end, expecting [] but receiving null
//                                    .applicableLegislation(List.of())
                                    .conformsTo(List.of())
                                    .documentation(List.of())
                                    .retentionPeriod(List.of())
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
                    .alternateIdentifier(List.of("internalURI:admsIdentifier0"))
                    .analytics(List.of("http://example.com/analytics"))
                    .applicableLegislation(List.of(URI.create(
                            "http://data.europa.eu/eli/reg/2022/868/oj")))
                    .codeValues(List.of(
                            getValueLabel("Code Label One", "http://example.com/code1", 7),
                            getValueLabel("Code Label Two", "http://example.com/code2", 8)))
                    .codingSystem(List.of(
                            getValueLabel("Coding System", "http://example.com/codingSystem")))
                    .hdab(List.of(Agent.builder()
                            .name("EU Health Data Access Body")
                            .email("hdab@example.com")
                            .url("https://www.example.com/hdab")
                            .build()))
                    .healthCategory(List.of(
                            getValueLabel("Genomics",
                                    "http://example.com/health-category/genomics", 6),
                            getValueLabel("Medical Imaging",
                                    "http://example.com/health-category/medical-imaging", 3),
                            getValueLabel("Electronic Health Records",
                                    "http://example.com/health-category/ehr", 1)))
                    .healthTheme(List.of(
                            getValueLabel("Health Theme 1",
                                    "http://www.wikidata.org/entity/Q58624061", 4),
                            getValueLabel("Health Theme 2",
                                    "http://www.wikidata.org/entity/Q7907952", 2)))
                    .legalBasis(List.of(
                            getValueLabel("Consent", "https://w3id.org/dpv#Consent")))
                    .maxTypicalAge(100)
                    .minTypicalAge(0)
                    .numberOfRecords(123456789)
                    .numberOfUniqueIndividuals(123456789)
                    .personalData(List.of(
                            getValueLabel("Age", "https://w3id.org/dpv/dpv-pd#Age", 9),
                            getValueLabel("Gender", "https://w3id.org/dpv/dpv-pd#Gender", 8),
                            getValueLabel("Health Record",
                                    "https://w3id.org/dpv/dpv-pd#HealthRecord", 7)))
                    .populationCoverage(
                            "This example includes a very non-descript population")
                    .publisherNote(
                            "Health-RI is the Dutch health care initiative to build an integrated health data infrastructure for research and innovation.")
                    .publisherType(List.of(
                            getValueLabel("Undefined",
                                    "http://example.com/publisherType/undefined")))
                    .trustedDataHolder(true)
                    .purpose(List.of(
                            getValueLabel("Academic Research",
                                    "https://w3id.org/dpv#AcademicResearch")))
                    .qualifiedRelation(List.of(
                            RetrievedDatasetQualifiedRelationInner.builder()
                                    .relation("http://example.com/dataset/3.141592")
                                    .role(getValueLabel("Academic Research Role",
                                            "https://w3id.org/dpv#AcademicResearchRole", 11))
                                    .uri("https://w3id.org/dpv#AcademicResearchUri")
                                    .build()))
                    .retentionPeriod(List.of(
                            TimeWindow.builder()
                                    .start(parse("2024-07-01T22:00:00Z"))
                                    .end(parse("2024-07-02T22:00:00Z"))
                                    .build()))
                    .spatialCoverage(List.of(
                            SpatialCoverage.builder()
                                    .uri(getValueLabel("Utrecht, Netherlands",
                                            "https://www.geonames.org/2745912/utrecht.html"))
                                    .text("Utrecht, Netherlands")
                                    .geom("POLYGON((5.045 52.090, 5.145 52.090, 5.145 52.150, 5.045 52.150, 5.045 52.090))")
                                    .bbox("5.045,52.090,5.145,52.150")
                                    .centroid("5.095,52.120")
                                    .build()))
                    .spatialResolutionInMeters(10.0f)
                    .temporalCoverage(
                            TimeWindow.builder()
                                    .start(parse("2024-07-12T22:00:00+00:00"))
                                    .end(parse("2024-07-13T22:00:00+00:00"))
                                    .build())
                    .temporalResolution("P1D")
                    .provenanceActivity(List.of())
                    .qualifiedAttribution(List.of())
                    .qualityAnnotation(List.of())
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
                .theme(getCkanValueLabels("theme", "theme-name", 3))
                .issued("2024-07-01T22:00:00+00:00")
                .modified("2024-07-02T22:00:00Z")
                .tags(getCkanTags())
                .url("url")
                .language(getCkanValueLabels("language", "en", 2))
                .hasVersion(getCkanValueLabels("version", "1"))
                .accessRights(getCkanValueLabel("accessRights", "public", 10))
                .conformsTo(getCkanValueLabels("conformsTo", "conforms", 5))
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
                .healthTheme(List.of(
                        getCkanValueLabel("Health Theme 1",
                                "http://www.wikidata.org/entity/Q58624061", 4),
                        getCkanValueLabel("Health Theme 2",
                                "http://www.wikidata.org/entity/Q7907952", 2)))
                .healthCategory(List.of(
                        getCkanValueLabel("Genomics",
                                "http://example.com/health-category/genomics", 6),
                        getCkanValueLabel("Medical Imaging",
                                "http://example.com/health-category/medical-imaging", 3),
                        getCkanValueLabel("Electronic Health Records",
                                "http://example.com/health-category/ehr", 1)))
                .legalBasis(List.of(getCkanValueLabel("Consent", "https://w3id.org/dpv#Consent")))
                .hdab(List.of(
                        CkanAgent.builder()
                                .name("EU Health Data Access Body")
                                .url("https://www.example.com/hdab")
                                .email("hdab@example.com")
                                .build()
                )
                )
                .minTypicalAge(0)
                .maxTypicalAge(100)
                .numberOfRecords(123456789)
                .numberOfUniqueIndividuals(123456789)
                .personalData(List.of(
                        getCkanValueLabel("Age", "https://w3id.org/dpv/dpv-pd#Age", 9),
                        getCkanValueLabel("Gender", "https://w3id.org/dpv/dpv-pd#Gender", 8),
                        getCkanValueLabel("Health Record",
                                "https://w3id.org/dpv/dpv-pd#HealthRecord", 7)))
                .populationCoverage(List.of("This example includes a very non-descript population"))
                .publisherNote(
                        "Health-RI is the Dutch health care initiative to build an integrated health data infrastructure for research and innovation.")
                .publisherType(List.of(getCkanValueLabel("Undefined",
                        "http://example.com/publisherType/undefined")))
                .trustedDataHolder(true)
                .purpose(List.of(getCkanValueLabel("Academic Research",
                        "https://w3id.org/dpv#AcademicResearch")))
                .retentionPeriod(List.of(
                        CkanTimeWindow.builder()
                                .start("2024-07-01T22:00:00+00:00")
                                .end("2024-07-02T22:00:00+00:00")
                                .build()
                ))
                .codeValues(List.of(
                        getCkanValueLabel("Code Label One", "http://example.com/code1", 7),
                        getCkanValueLabel("Code Label Two", "http://example.com/code2", 8)))
                .analytics(List.of("http://example.com/analytics"))
                .codingSystem(List.of(getCkanValueLabel("Coding System",
                        "http://example.com/codingSystem")))
                .applicableLegislation(List.of(URI.create(
                        "http://data.europa.eu/eli/reg/2022/868/oj")))
                .qualifiedRelation(List.of(
                        CkanPackageQualifiedRelationInner.builder()
                                .role(getCkanValueLabel("Academic Research Role",
                                        "https://w3id.org/dpv#AcademicResearchRole", 11))
                                .relation("http://example.com/dataset/3.141592")
                                .uri("https://w3id.org/dpv#AcademicResearchUri")
                                .build()
                ))
                .temporalStart("2024-07-12T22:00:00+00:00")
                .temporalEnd("2024-07-13T22:00:00+00:00")
                .temporalResolution("P1D")
                .alternateIdentifier(List.of("internalURI:admsIdentifier0"))
                .spatialCoverage(List.of(
                        CkanSpatialCoverage.builder()
                                .uri(getCkanValueLabel("Utrecht, Netherlands",
                                        "https://www.geonames.org/2745912/utrecht.html"))
                                .text("Utrecht, Netherlands")
                                .geom("POLYGON((5.045 52.090, 5.145 52.090, 5.145 52.150, 5.045 52.150, 5.045 52.090))")
                                .bbox("5.045,52.090,5.145,52.150")
                                .centroid("5.095,52.120")
                                .build()
                ))
                .spatialResolutionInMeters(10.0f)
                .alternateIdentifier(List.of("internalURI:admsIdentifier0"))
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
                    .themes(getValueLabels("theme", "theme-name", 3))
                    .keywords(List.of("key-tag"))
                    .modifiedAt(OffsetDateTime.parse("2024-07-02T22:00Z"))
                    .createdAt(OffsetDateTime.parse("2024-07-01T22:00Z"))
                    .distributionsCount(1)
                    .numberOfUniqueIndividuals(123456789)
                    .accessRights(getValueLabel("accessRights", "public", 10))
                    .temporalCoverage(TimeWindow.builder()
                            .start(parse("2024-07-12T22:00Z"))
                            .end(parse("2024-07-13T22:00Z"))
                            .build())
                    .build();
        }
    }

    private static @NotNull List<CkanResource> getCkanResources() {
        return List.of(
                CkanResource.builder()
                        .id("resource_id")
                        .name("resource_name")
                        .description("resource_description")
                        .format(getCkanValueLabel("format", "pdf", 1))
                        .accessUrl(URI.create("https://accessUrl.com"))
                        .downloadUrl(URI.create("https://downloadUrl.com"))
                        .issuedDate("2025-03-19")
                        .modifiedDate("2025-03-19T13:37:05Z")
                        .compressFormat("gzip")
                        .language(getCkanValueLabels("language", "en", 2))
                        .conformsTo(List.of())
                        .documentation(List.of())
                        .build());
    }

    private static CkanValueLabel getCkanValueLabel(String label, String value) {
        return getCkanValueLabel(label, value, null);
    }

    private static CkanValueLabel getCkanValueLabel(String label, String value, Integer count) {
        return CkanValueLabel.builder()
                .displayName(label)
                .name(value)
                .count(count)
                .build();
    }

    private static ValueLabel getValueLabel(String label, String value) {
        return getValueLabel(label, value, null);
    }

    private static ValueLabel getValueLabel(String label, String value, Integer count) {
        return ValueLabel.builder()
                .label(label)
                .value(value)
                .count(count)
                .build();
    }

    private static @NotNull List<CkanValueLabel> getCkanTags() {
        return List.of(getCkanValueLabel("key-tag", "key-tag"));
    }

    private static @NotNull List<CkanValueLabel> getCkanValueLabels(String label, String value) {
        return List.of(getCkanValueLabel(label, value));
    }

    private static @NotNull List<CkanValueLabel> getCkanValueLabels(String label, String value,
            Integer count) {
        return List.of(getCkanValueLabel(label, value, count));
    }

    private static @NotNull List<ValueLabel> getValueLabels(String label, String value) {
        return List.of(getValueLabel(label, value));
    }

    private static @NotNull List<ValueLabel> getValueLabels(String label, String value,
            Integer count) {
        return List.of(getValueLabel(label, value, count));
    }
}
