// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.mapper;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.mapstruct.CollectionMappingStrategy.ADDER_PREFERRED;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

@Mapper(componentModel = "jakarta", nullValueCheckStrategy = ALWAYS, nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, collectionMappingStrategy = ADDER_PREFERRED)
public interface CkanDatasetsMapper {

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    @Mapping(target = "description", source = "notes")
    @Mapping(target = "themes", source = "theme")
    @Mapping(target = "contacts", source = "contact")
    @Mapping(target = "distributions", source = ".", qualifiedByName = "filterDistributions")
    @Mapping(target = "keywords", source = ".", qualifiedByName = "keywords")
    @Mapping(target = "spatial", source = "spatialUri")
    @Mapping(target = "createdAt", source = "issued")
    @Mapping(target = "modifiedAt", source = "modified")
    @Mapping(target = "creators", source = "creator")
    @Mapping(target = "hasVersions", source = "hasVersion")
    @Mapping(target = "publishers", source = "publisher")
    @Mapping(target = "languages", source = "language")
    @Mapping(target = "dcatType", source = "dcatType")
    @Mapping(target = "catalogue", ignore = true)
    @Mapping(target = "qualifiedAttribution", source = "qualifiedAttribution")
    @Mapping(target = "provenanceActivity", source = "wasGeneratedBy") // Discrepancy between CKAN field name and Discovery Service field name to not break existing mapping. CKAN field is "was_generated_by" and Discovery Service field is "provenanceActivity" for now.
    @Mapping(target = "qualityAnnotation", source = "qualityAnnotation")
    @Mapping(target = "homepage", source = "homepage")
    @Mapping(target = "uri", source = "uri")
    @Mapping(target = "documentation", source = "documentation")
    @Mapping(target = "frequency", source = "frequency")
    @Mapping(target = "inSeries", ignore = true)
    @Mapping(target = "isReferencedBy", source = "isReferencedBy")
    @Mapping(target = "temporalCoverage", source = "temporalCoverage")
    @Mapping(target = "retentionPeriod", source = "retentionPeriod")
    @Mapping(target = "spatialCoverage", source = "spatialCoverage")
    @Mapping(target = "accessRights", source = "accessRights")
    @Mapping(target = "status", source = "datasetStatus")
    @Mapping(target = "analytics", source = ".", qualifiedByName = "extractAnalytics")
    @Mapping(target = "samples", source = ".", qualifiedByName = "extractSamples")
    @Mapping(target = "applicableLegislation", source = "applicableLegislation")
    @Mapping(target = "codeValues", source = "codeValues")
    @Mapping(target = "codingSystem", source = "codingSystem")
    @Mapping(target = "conformsTo", source = "conformsTo")
    @Mapping(target = "legalBasis", source = "legalBasis")
    @Mapping(target = "maxTypicalAge", source = "maxTypicalAge")
    @Mapping(target = "minTypicalAge", source = "minTypicalAge")
    @Mapping(target = "numberOfRecords", source = "numberOfRecords")
    @Mapping(target = "numberOfUniqueIndividuals", source = "numberOfUniqueIndividuals")
    @Mapping(target = "alternateIdentifier", source = "alternateIdentifier")
    @Mapping(target = "personalData", source = "personalData")
    @Mapping(target = "populationCoverage", source = "populationCoverage")
    @Mapping(target = "purpose", source = "purpose")
    @Mapping(target = "qualifiedRelation", source = "qualifiedRelation")
    @Mapping(target = "temporalResolution", source = "temporalResolution")
    @Mapping(target = "healthTheme", source = "healthTheme")
    @Mapping(target = "versionNotes", source = "versionNotes")
    @Mapping(target = "alternativeTitle", source = "alternativeTitle")
    @Mapping(target = "hasStructuredData", source = "hasStructuredData")
    @Mapping(target = "relatedResource", source = "relatedResource")
    @Mapping(target = "source", source = "source")
    @Mapping(target = "custodian", source = "custodian")
    @Mapping(target = "hdabCoordinator", source = "hdabCoordinator")
    @Mapping(target = "variables", source = "variables")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "ownerOrg", source = "ownerOrg")
    @Mapping(target = "isSeries", source = ".", qualifiedByName = "isDatasetSeries")
    @Mapping(target = "helpText", ignore = true)
    RetrievedDataset map(CkanPackage ckanPackage);

    @Mapping(target = "contacts", source = "contact")
    @Mapping(target = "description", source = "notes")
    @Mapping(target = "frequency", source = "frequency")
    @Mapping(target = "spatial", source = ".", qualifiedByName = "toGeographicalCoverageFromPackage")
    @Mapping(target = "modified", source = "modified")
    @Mapping(target = "publishers", source = "publisher")
    @Mapping(target = "issued", source = "issued")
    @Mapping(target = "temporalCoverage", source = "temporalCoverage")
    @Mapping(target = "applicableLegislation", source = "applicableLegislation")
    DatasetSeries mapToDatasetSeries(CkanPackage ckanPackage);

    default ValueLabel map(CkanValueLabel ckanValueLabel) {
        if (ckanValueLabel == null) {
            return null;
        }

        var value = StringUtils.trimToNull(ckanValueLabel.getName());
        var label = StringUtils.trimToNull(ckanValueLabel.getDisplayName());

        if (value == null && label == null) {
            return null;
        }

        if (value == null) {
            value = label;
        }

        if (label == null) {
            label = value;
        }

        return ValueLabel.builder()
                .value(value)
                .label(label)
                .count(ckanValueLabel.getCount())
                .build();
    }

    @Mapping(target = "accessUrl", source = "accessUrl")
    @Mapping(target = "downloadUrl", source = "downloadUrl")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "format", source = "format")
    @Mapping(target = "languages", source = "language")
    @Mapping(target = "retentionPeriod", source = "retentionPeriod")
    @Mapping(target = "accessService", source = "accessServices")
    @Mapping(target = "createdAt", source = "issued")
    @Mapping(target = "modifiedAt", source = "modified")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "license", source = "license")
    @Mapping(target = "mediaType", source = "mimetype")
    @Mapping(target = "compressionFormat", source = "compressFormat")
    @Mapping(target = "packagingFormat", source = "packageFormat")
    @Mapping(target = "byteSize", source = "size")
    @Mapping(target = "checksum", source = ".", qualifiedByName = "checksumValue")
    @Mapping(target = "checksumAlgorithm", source = ".", qualifiedByName = "checksumAlgorithm")
    @Mapping(target = "hasPolicy", source = "hasPolicy")
    @Mapping(target = "rights", source = "rights")
    @Mapping(target = "availability", source = "availability")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "temporalResolution", source = "temporalResolution")
    @Mapping(target = "spatialResolutionInMeters", source = "spatialResolutionInMeters")
    @Mapping(target = "conformsTo", source = "conformsTo")
    @Mapping(target = "applicableLegislation", source = "applicableLegislation")
    RetrievedDistribution map(CkanResource ckanResource);

    @Mapping(target = "id", source = "identifier")
    @Mapping(target = "keywords", source = "keyword")
    @Mapping(target = "languages", source = "language")
    @Mapping(target = "license", source = "license")
    @Mapping(target = "accessRights", source = "accessRights")
    @Mapping(target = "applicableLegislation", source = "applicableLegislation")
    @Mapping(target = "conformsTo", source = "conformsTo")
    @Mapping(target = "contact", source = "contact")
    @Mapping(target = "creator", source = "creator")
    @Mapping(target = "rights", source = "rights")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "documentation", source = "documentation")
    @Mapping(target = "endpointDescription", source = "endpointDescription")
    @Mapping(target = "endpointUrl", source = "endpointUrl")
    @Mapping(target = "format", source = "format")
    @Mapping(target = "landingPage", source = "landingPage")
    @Mapping(target = "modified", source = "modified")
    @Mapping(target = "publisher", source = "publisher")
    @Mapping(target = "servesDataset", source = "servesDataset")
    @Mapping(target = "theme", source = "theme")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "hvdCategory", source = "hvdCategory")
    AccessServiceInner map(CkanResourceAccessServicesInner source);

    default List<SearchedDataset> map(PackagesSearchResult result) {
        if (result == null || result.getResults() == null) {
            return Collections.emptyList();
        }
        return result.getResults().stream()
                .map(this::mapToSearchedDataset)
                .toList();
    }

    @Mapping(target = "description", source = "notes")
    @Mapping(target = "themes", source = "theme")
    @Mapping(target = "publishers", source = "publisher")
    @Mapping(target = "datasetType", source = ".", qualifiedByName = "mapDatasetType")
    @Mapping(target = "keywords", source = ".", qualifiedByName = "keywords")
    @Mapping(target = "modifiedAt", source = "modified")
    @Mapping(target = "createdAt", source = "issued")
    @Mapping(target = "accessRights", source = "accessRights")
    @Mapping(target = "conformsTo", source = "conformsTo")
    @Mapping(target = "numberOfUniqueIndividuals", source = "numberOfUniqueIndividuals")
    @Mapping(target = "temporalCoverage", source = "temporalCoverage")
    @Mapping(target = "temporalCoverageStart", source = "temporalCoverage", qualifiedByName = "mapEarliestTemporalCoverageStart")
    @Mapping(target = "temporalCoverageEnd", source = "temporalCoverage", qualifiedByName = "mapLatestTemporalCoverageEnd")
    @Mapping(target = "distributionsCount", source = ".", qualifiedByName = "countDistributions")
    @Mapping(target = "inSeriesCount", source = ".", qualifiedByName = "countInSeries")
    @Mapping(target = "isSeries", source = ".", qualifiedByName = "isDatasetSeries")
    @Mapping(target = "catalogue", ignore = true)
    @Mapping(target = "recordsCount", ignore = true)
    SearchedDataset mapToSearchedDataset(CkanPackage ckanPackage);

    @Named("mapDatasetType")
    default String mapDatasetType(CkanPackage ckanPackage) {
        if (ckanPackage == null) {
            return null;
        }

        if (ckanPackage.getDcatType() != null) {
            // The search result shows one type: the first usable one of the list.
            var dcatDatasetType = ckanPackage.getDcatType().stream()
                    .filter(Objects::nonNull)
                    .flatMap(type -> Stream.of(type.getDisplayName(), type.getName()))
                    .map(StringUtils::trimToNull)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            if (dcatDatasetType != null) {
                return dcatDatasetType;
            }
        }

        return StringUtils.trimToNull(ckanPackage.getDatasetType());
    }

    default List<TimeWindow> map(List<CkanTimeWindow> temporalCoverage) {
        if (temporalCoverage == null || temporalCoverage.isEmpty()) {
            return Collections.emptyList();
        }

        return temporalCoverage.stream()
                .filter(this::hasTemporalCoverageValue)
                .map(this::map)
                .toList();
    }

    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    TimeWindow map(CkanTimeWindow timeWindow);

    default boolean hasTemporalCoverageValue(CkanTimeWindow timeWindow) {
        if (timeWindow == null) {
            return false;
        }

        return StringUtils.isNotBlank(timeWindow.getStart())
                || StringUtils.isNotBlank(timeWindow.getEnd());
    }

    @Named("mapEarliestTemporalCoverageStart")
    default OffsetDateTime mapEarliestTemporalCoverageStart(
            List<CkanTimeWindow> temporalCoverage) {
        if (temporalCoverage == null || temporalCoverage.isEmpty()) {
            return null;
        }

        return temporalCoverage.stream()
                .filter(Objects::nonNull)
                .map(CkanTimeWindow::getStart)
                .filter(StringUtils::isNotBlank)
                .map(this::map)
                .filter(Objects::nonNull)
                .min(OffsetDateTime::compareTo)
                .orElse(null);
    }

    @Named("mapLatestTemporalCoverageEnd")
    default OffsetDateTime mapLatestTemporalCoverageEnd(
            List<CkanTimeWindow> temporalCoverage) {
        if (temporalCoverage == null || temporalCoverage.isEmpty()) {
            return null;
        }

        return temporalCoverage.stream()
                .filter(Objects::nonNull)
                .map(CkanTimeWindow::getEnd)
                .filter(StringUtils::isNotBlank)
                .map(this::map)
                .filter(Objects::nonNull)
                .max(OffsetDateTime::compareTo)
                .orElse(null);
    }

    @Mapping(target = "uri", source = "uri")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "geom", source = "geom")
    @Mapping(target = "bbox", source = "bbox")
    @Mapping(target = "centroid", source = "centroid")
    SpatialCoverage map(CkanSpatialCoverage spatialCoverage);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "url", source = "url")
    @Mapping(target = "identifier", source = "identifier")
    ContactPoint map(CkanContactPoint ckanContactPoint);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "url", source = "url")
    @Mapping(target = "homepage", source = "homepage")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "publisherNote", source = "publisherNote")
    @Mapping(target = "publisherType", source = "publisherType", qualifiedByName = "firstPublisherType")
    @Mapping(target = "identifier", source = "identifier")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "actedOnBehalfOf", source = "actedOnBehalfOf")
    @Mapping(target = "uri", source = "uri")
    @Mapping(target = "telephone", source = "telephone")
    @Mapping(target = "openingHours", source = "openingHours")
    @Mapping(target = "specialOpeningHours", source = "specialOpeningHours")
    @Mapping(target = "spatial", ignore = true)
    Agent map(CkanAgent ckanAgent);

    /**
     * The publisher type of an agent has cardinality 0..1, while CKAN stores a list. Takes the
     * first usable type, in the order CKAN returns them.
     */
    @Named("firstPublisherType")
    default ValueLabel firstPublisherType(List<CkanValueLabel> publisherTypes) {
        if (publisherTypes == null) {
            return null;
        }

        return publisherTypes.stream()
                .map(this::map)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Mapping(target = "title", source = ".", qualifiedByName = "variableGroupTitle")
    VariableGroup map(CkanVariableGroup group);

    @Mapping(target = "title", source = ".", qualifiedByName = "variableTableTitle")
    VariableTable map(CkanVariableTable table);

    @Mapping(target = "title", source = ".", qualifiedByName = "variableColumnTitle")
    @Mapping(target = "description", source = ".", qualifiedByName = "variableColumnDescription")
    VariableColumn map(CkanVariableColumn column);

    /*
     * A variable title is the text in the language of the request that CKAN resolved, or, when a
     * title was typed without a language, the text of title_translated.
     */
    @Named("variableGroupTitle")
    default String variableGroupTitle(CkanVariableGroup group) {
        return StringUtils.trimToNull(StringUtils.firstNonBlank(group.getTitle(), textOf(group
                .getTitleTranslated())));
    }

    @Named("variableTableTitle")
    default String variableTableTitle(CkanVariableTable table) {
        return StringUtils.trimToNull(StringUtils.firstNonBlank(table.getTitle(), textOf(table
                .getTitleTranslated())));
    }

    @Named("variableColumnTitle")
    default String variableColumnTitle(CkanVariableColumn column) {
        return StringUtils.trimToNull(StringUtils.firstNonBlank(column.getTitle(), textOf(column
                .getTitleTranslated())));
    }

    @Named("variableColumnDescription")
    default String variableColumnDescription(CkanVariableColumn column) {
        return StringUtils.trimToNull(StringUtils.firstNonBlank(column.getDescription(), textOf(
                column.getDescriptionTranslated())));
    }

    private static String textOf(CkanLocalizedText text) {
        return text == null ? null : text.getText();
    }

    /**
     * A rights statement as a value and label. The value is the IRI of the statement, or its text
     * when it has no IRI, as for any other plain text served where a value and label are expected.
     * The label is the text, or the IRI when the statement has no text.
     */
    default ValueLabel map(CkanRight right) {
        var value = StringUtils.firstNonBlank(right.getUri(), right.getLabel());
        if (value == null) {
            return null;
        }

        return ValueLabel.builder()
                .value(value)
                .label(StringUtils.firstNonBlank(right.getLabel(), right.getUri()))
                .build();
    }

    /**
     * Rights statements of a distribution. gdi_current has one text and R8 a list of statements;
     * both arrive as a list, and a statement without an IRI and a label is left out.
     */
    default List<ValueLabel> mapRights(List<CkanRight> rights) {
        if (rights == null) {
            return Collections.emptyList();
        }

        return rights.stream()
                .filter(Objects::nonNull)
                .map(this::map)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * The checksum is the plain {@code hash} of gdi_current, or the checksum value of the entity
     * that R8 has.
     */
    @Named("checksumValue")
    default String checksumValue(CkanResource resource) {
        return StringUtils.firstNonBlank(
                resource.getHash(),
                firstChecksum(resource).map(CkanChecksum::getChecksumValue).orElse(null));
    }

    @Named("checksumAlgorithm")
    default ValueLabel checksumAlgorithm(CkanResource resource) {
        var algorithm = resource.getHashAlgorithm() != null
                ? resource.getHashAlgorithm()
                : firstChecksum(resource).map(CkanChecksum::getAlgorithm).orElse(null);

        return map(algorithm);
    }

    private static Optional<CkanChecksum> firstChecksum(CkanResource resource) {
        if (resource.getChecksum() == null) {
            return Optional.empty();
        }

        return resource.getChecksum().stream()
                .filter(Objects::nonNull)
                .findFirst();
    }

    default OffsetDateTime map(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }

        try {
            return OffsetDateTime.parse(date);
        } catch (DateTimeParseException e) {
            var dateToParse = date;
            if (dateToParse.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")) {
                dateToParse += "T00:00:00.000000";
            }
            return LocalDateTime.parse(dateToParse, DATE_FORMATTER)
                    .truncatedTo(ChronoUnit.SECONDS)
                    .atOffset(ZoneOffset.UTC);
        }
    }

    /**
     * Returns the keywords of the package. CKAN already selected them in the language of the
     * request (falling back to English), so tags_translated, which holds every language, is not
     * used here.
     */
    @Named("keywords")
    default List<String> keywords(CkanPackage ckanPackage) {
        if (ckanPackage == null || ckanPackage.getTags() == null) {
            return Collections.emptyList();
        }

        return ckanPackage.getTags().stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .distinct()
                .toList();
    }

    /**
     * Filters the distributions list to exclude samples and analytics distributions.
     * Returns only the distributions that are not referenced in the sample or analytics arrays.
     */
    @Named("filterDistributions")
    default List<RetrievedDistribution> filterDistributions(CkanPackage ckanPackage) {
        if (ckanPackage == null || ckanPackage.getResources() == null) {
            return Collections.emptyList();
        }

        List<String> sampleUris = ckanPackage.getSample() != null ? ckanPackage.getSample()
                : Collections.emptyList();
        List<String> analyticsUris = ckanPackage.getAnalytics() != null ? ckanPackage.getAnalytics()
                : Collections.emptyList();

        // Combine all excluded URIs
        Stream<String> excludedUris = Stream.concat(sampleUris.stream(), analyticsUris.stream());
        Set<String> excludedUriSet = excludedUris.collect(java.util.stream.Collectors.toSet());

        // Map and filter resources
        return ckanPackage.getResources().stream()
                .filter(resource -> !excludedUriSet.contains(resource.getUri()))
                .map(this::map)
                .toList();
    }

    /**
     * Extracts the sample distributions.
     * Maps the sample URIs to their corresponding distribution objects.
     */
    @Named("extractSamples")
    default List<RetrievedDistribution> extractSamples(CkanPackage ckanPackage) {
        if (ckanPackage == null) {
            return Collections.emptyList();
        }

        List<String> sampleUris = ckanPackage.getSample();
        return extractByUris(
                ckanPackage,
                sampleUris != null ? sampleUris : Collections.emptyList());
    }

    /**
     * Extracts the analytics distributions.
     * Maps the analytics URIs to their corresponding distribution objects.
     */
    @Named("extractAnalytics")
    default List<RetrievedDistribution> extractAnalytics(CkanPackage ckanPackage) {
        if (ckanPackage == null) {
            return Collections.emptyList();
        }

        List<String> analyticsUris = ckanPackage.getAnalytics();
        return extractByUris(
                ckanPackage,
                analyticsUris != null ? analyticsUris : Collections.emptyList());
    }

    /**
     * Counts the distributions excluding samples and analytics distributions.
     * Delegates to {@link #filterDistributions(CkanPackage)} to ensure the
     * exclusion rules are defined in a single place.
     */
    @Named("countDistributions")
    default int countDistributions(CkanPackage ckanPackage) {
        return filterDistributions(ckanPackage).size();
    }

    /**
     * Counts unique dataset series references, ignoring null/blank values.
     */
    @Named("countInSeries")
    default int countInSeries(CkanPackage ckanPackage) {
        if (ckanPackage == null || ckanPackage.getInSeries() == null) {
            return 0;
        }

        return (int) ckanPackage.getInSeries().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .count();
    }

    @Named("isDatasetSeries")
    default Boolean isDatasetSeries(CkanPackage ckanPackage) {
        if (ckanPackage == null || ckanPackage.getType() == null) {
            return Boolean.FALSE;
        }

        var packageType = Stream.of(ckanPackage.getType().getName(),
                ckanPackage.getType().getDisplayName())
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .map(String::trim)
                .orElse("");

        return "dataset_series".equalsIgnoreCase(packageType);
    }

    /**
     * Extracts distributions whose URIs are listed in the provided collection.
     * Shared helper used by extractSamples and extractAnalytics.
     */
    default List<RetrievedDistribution> extractByUris(CkanPackage ckanPackage,
            List<String> uris) {
        if (ckanPackage == null || uris == null || uris.isEmpty()) {
            return Collections.emptyList();
        }

        List<CkanResource> resources = ckanPackage.getResources() != null ? ckanPackage
                .getResources()
                : Collections.emptyList();

        Set<String> uriSet = new HashSet<>(uris);

        return resources.stream()
                .filter(resource -> uriSet.contains(resource.getUri()))
                .map(this::map)
                .toList();
    }

    @Named("toGeographicalCoverageFromPackage")
    default List<ValueLabel> toGeographicalCoverageFromPackage(CkanPackage ckanPackage) {
        if (ckanPackage == null) {
            return Collections.emptyList();
        }

        var fromSpatialCoverage = java.util.Optional.ofNullable(ckanPackage.getSpatialCoverage())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(this::mapSpatialCoverageToValueLabel)
                .filter(v -> v != null && (StringUtils.isNotBlank(v.getValue()) || StringUtils
                        .isNotBlank(v.getLabel())))
                .toList();

        if (!fromSpatialCoverage.isEmpty()) {
            return fromSpatialCoverage;
        }

        if (ckanPackage.getSpatialUri() == null) {
            return Collections.emptyList();
        }

        return List.of(map(ckanPackage.getSpatialUri()));
    }

    default ValueLabel mapSpatialCoverageToValueLabel(CkanSpatialCoverage spatialCoverage) {
        if (spatialCoverage == null) {
            return null;
        }

        var uri = spatialCoverage.getUri();
        var value = uri != null ? uri.getName() : spatialCoverage.getText();
        var label = uri != null ? uri.getDisplayName() : spatialCoverage.getText();

        return ValueLabel.builder()
                .value(value)
                .label(label)
                .build();
    }
}
