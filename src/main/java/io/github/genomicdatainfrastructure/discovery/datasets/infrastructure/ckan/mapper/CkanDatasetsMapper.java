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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    @Mapping(target = "keywords", source = ".", qualifiedByName = "mergeKeywords")
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
    @Mapping(target = "provenanceActivity", source = "provenanceActivity")
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
    @Mapping(target = "version", source = "version")
    @Mapping(target = "ownerOrg", source = "ownerOrg")
    @Mapping(target = "isSeries", source = ".", qualifiedByName = "isDatasetSeries")
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

    @Mapping(target = "label", source = "displayName")
    @Mapping(target = "value", source = "name")
    @Mapping(target = "count", source = "count")
    ValueLabel map(CkanValueLabel ckanValueLabel);

    @Mapping(target = "accessUrl", source = "accessUrl")
    @Mapping(target = "downloadUrl", source = "downloadUrl")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "format", source = "format")
    @Mapping(target = "languages", source = "language")
    @Mapping(target = "retentionPeriod", source = "retentionPeriod")
    @Mapping(target = "accessService", source = "accessServices")
    @Mapping(target = "createdAt", source = "issuedDate")
    @Mapping(target = "modifiedAt", source = "modifiedDate")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "license", source = "license")
    @Mapping(target = "mediaType", source = "mimetype")
    @Mapping(target = "compressionFormat", source = "compressFormat")
    @Mapping(target = "packagingFormat", source = "packageFormat")
    @Mapping(target = "byteSize", source = "size")
    @Mapping(target = "checksum", source = "hash")
    @Mapping(target = "checksumAlgorithm", source = "hashAlgorithm")
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
    @Mapping(target = "keywords", source = ".", qualifiedByName = "mergeKeywords")
    @Mapping(target = "modifiedAt", source = "modified")
    @Mapping(target = "createdAt", source = "issued")
    @Mapping(target = "accessRights", source = "accessRights")
    @Mapping(target = "conformsTo", source = "conformsTo")
    @Mapping(target = "numberOfUniqueIndividuals", source = "numberOfUniqueIndividuals")
    @Mapping(target = "temporalCoverage", source = "temporalCoverage")
    @Mapping(target = "distributionsCount", source = ".", qualifiedByName = "countDistributions")
    @Mapping(target = "inSeriesCount", source = ".", qualifiedByName = "countInSeries")
    @Mapping(target = "isSeries", source = ".", qualifiedByName = "isDatasetSeries")
    @Mapping(target = "catalogue", ignore = true)
    @Mapping(target = "recordsCount", ignore = true)
    SearchedDataset mapToSearchedDataset(CkanPackage ckanPackage);

    default List<TimeWindow> map(List<CkanTimeWindow> temporalCoverage) {
        if (temporalCoverage == null || temporalCoverage.isEmpty()) {
            return Collections.emptyList();
        }

        return temporalCoverage.stream()
                .filter(Objects::nonNull)
                .map(this::map)
                .toList();
    }

    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    TimeWindow map(CkanTimeWindow timeWindow);

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
    @Mapping(target = "identifier", source = "identifier")
    @Mapping(target = "actedOnBehalfOf", source = "actedOnBehalfOf")
    @Mapping(target = "spatial", ignore = true)
    Agent map(CkanAgent ckanAgent);

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
     * Merges keywords from both tags (array of strings) and tags_translated (multilingual map).
     * CKAN stores tags differently depending on how they were added:
     * - Harvested datasets have tags in the 'tags' field
     * - Manually added tags via web UI are stored in 'tags_translated'
     * This method combines both sources and removes duplicates.
     */
    @Named("mergeKeywords")
    default List<String> mergeKeywords(CkanPackage ckanPackage) {
        if (ckanPackage == null) {
            return Collections.emptyList();
        }

        List<String> tagsFromField = ckanPackage.getTags();
        Map<String, List<String>> tagsTranslated = ckanPackage.getTagsTranslated();

        List<String> keywords = new ArrayList<>();

        // Add tags from the standard tags field
        if (tagsFromField != null) {
            tagsFromField.stream()
                    .filter(StringUtils::isNotBlank)
                    .map(String::trim)
                    .forEach(keywords::add);
        }

        // Add tags from tags_translated (all languages)
        if (tagsTranslated != null) {
            tagsTranslated.values().stream()
                    .flatMap(List::stream)
                    .filter(StringUtils::isNotBlank)
                    .map(String::trim)
                    .forEach(keywords::add);
        }

        // Remove duplicates while preserving order
        return keywords.stream()
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
            return null;
        }

        var packageType = Stream.of(ckanPackage.getType().getName(),
                ckanPackage.getType().getDisplayName())
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .map(String::trim)
                .orElse(null);

        if (StringUtils.isBlank(packageType)) {
            return null;
        }

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
