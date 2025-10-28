// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.mapper;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mapstruct.CollectionMappingStrategy.ADDER_PREFERRED;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

@Mapper(componentModel = "jakarta", nullValueCheckStrategy = ALWAYS, nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, collectionMappingStrategy = ADDER_PREFERRED)
public interface CkanDatasetsMapper {

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    @Mapping(target = "description", source = "notes")
    @Mapping(target = "themes", source = "theme")
    @Mapping(target = "contacts", source = "contact")
    @Mapping(target = "distributions", source = "resources")
    @Mapping(target = "keywords", source = "tags")
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
    @Mapping(target = "inSeries", source = "inSeries")
    @Mapping(target = "isReferencedBy", source = "isReferencedBy")
    @Mapping(target = "temporalCoverage.start", source = "temporalStart")
    @Mapping(target = "temporalCoverage.end", source = "temporalEnd")
    @Mapping(target = "retentionPeriod", source = "retentionPeriod")
    @Mapping(target = "spatialCoverage", source = "spatialCoverage")
    @Mapping(target = "accessRights", source = "accessRights")
    @Mapping(target = "analytics", source = "analytics")
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
    RetrievedDataset map(CkanPackage ckanPackage);

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
    @Mapping(target = "uri", source = "uri")
    @Mapping(target = "title", source = "title")
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
    @Mapping(target = "keywords", source = "tags")
    @Mapping(target = "modifiedAt", source = "modified")
    @Mapping(target = "createdAt", source = "issued")
    @Mapping(target = "accessRights", source = "accessRights")
    @Mapping(target = "numberOfUniqueIndividuals", source = "numberOfUniqueIndividuals")
    @Mapping(target = "temporalCoverage.start", source = "temporalStart")
    @Mapping(target = "temporalCoverage.end", source = "temporalEnd")
    @Mapping(target = "distributionsCount", expression = "java(ckanPackage.getResources()!= null ? ckanPackage.getResources().size():0)")
    @Mapping(target = "catalogue", ignore = true)
    @Mapping(target = "recordsCount", ignore = true)
    SearchedDataset mapToSearchedDataset(CkanPackage ckanPackage);

    @Mapping(target = "start", source = "start")
    @Mapping(target = "end", source = "end")
    TimeWindow map(CkanTimeWindow timeWindow);

    @Mapping(target = "uri", source = "uri")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "geom", source = "geom")
    @Mapping(target = "bbox", source = "bbox")
    @Mapping(target = "centroid", source = "centroid")
    SpatialCoverage map(CkanSpatialCoverage spatialCoverage);

    default List<String> map(List<CkanValueLabel> values) {
        if (values == null) {
            return Collections.emptyList();
        }
        return values.stream()
                .map(CkanValueLabel::getName)
                .toList();
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
}
