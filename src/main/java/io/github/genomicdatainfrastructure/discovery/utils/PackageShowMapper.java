// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.utils;

import static java.util.Optional.*;
import static java.util.function.Predicate.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import lombok.experimental.UtilityClass;

// TODO review original field and date format on resources
// TODO Remove duplicated code
@UtilityClass
public class PackageShowMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    );

    public RetrievedDataset from(CkanPackage ckanPackage) {
        var catalogue = ofNullable(ckanPackage.getOrganization())
                .map(CkanOrganization::getTitle)
                .orElse(null);

        return RetrievedDataset.builder()
                .id(ckanPackage.getId())
                .identifier(ckanPackage.getIdentifier())
                .title(ckanPackage.getTitle())
                .description(ckanPackage.getNotes())
                .themes(values(ckanPackage.getTheme()))
                .publisherName(ckanPackage.getPublisherName())
                .catalogue(catalogue)
                .organization(DatasetOrganizationMapper.from(ckanPackage.getOrganization()))
                .createdAt(parse(ckanPackage.getIssued()))
                .modifiedAt(parse(ckanPackage.getModified()))
                .url(ckanPackage.getUrl())
                .languages(values(ckanPackage.getLanguage()))
                .contact(value(ckanPackage.getContactUri()))
                .creators(creator(ckanPackage))
                .hasVersions(values(ckanPackage.getHasVersion()))
                .accessRights(value(ckanPackage.getAccessRights()))
                .conformsTo(values(ckanPackage.getConformsTo()))
                .provenance(ckanPackage.getProvenance())
                .spatial(value(ckanPackage.getSpatialUri()))
                .distributions(distributions(ckanPackage))
                .keywords(keywords(ckanPackage))
                .contacts(contactPoint(ckanPackage.getContactPoint()))
                .datasetRelationships(relations(ckanPackage.getDatasetRelationships()))
                .dataDictionary(dictionary(ckanPackage.getDataDictionary()))
                .build();
    }

    private List<ContactPoint> contactPoint(List<CkanContactPoint> values) {
        return ofNullable(values)
                .orElseGet(List::of)
                .stream()
                .filter(Objects::nonNull)
                .map(PackageShowMapper::contactPointEntry)
                .toList();
    }

    private List<DatasetRelationEntry> relations(List<CkanDatasetRelationEntry> values) {
        return ofNullable(values)
                .orElseGet(List::of)
                .stream()
                .filter(Objects::nonNull)
                .map(PackageShowMapper::relation)
                .toList();
    }

    private List<DatasetDictionaryEntry> dictionary(List<CkanDatasetDictionaryEntry> values) {
        return ofNullable(values)
                .orElseGet(List::of)
                .stream()
                .filter(Objects::nonNull)
                .map(PackageShowMapper::dictionaryEntry)
                .toList();
    }

    private ContactPoint contactPointEntry(CkanContactPoint value) {
        return ContactPoint.builder()
                .name(value.getContactName())
                .email(value.getContactEmail())
                .uri(value.getContactUri())
                .build();
    }

    private List<ValueLabel> creator(CkanPackage ckanPackage) {
        return ofNullable(ckanPackage.getCreator())
                .orElseGet(List::of)
                .stream()
                .map(PackageShowMapper::creator)
                .toList();
    }

    private DatasetRelationEntry relation(CkanDatasetRelationEntry value) {
        return DatasetRelationEntry.builder()
                .relation(value.getRelation())
                .target(value.getTarget())
                .build();
    }

    private DatasetDictionaryEntry dictionaryEntry(CkanDatasetDictionaryEntry value) {
        return DatasetDictionaryEntry.builder()
                .name(value.getName())
                .type(value.getType())
                .description(value.getDescription())
                .build();
    }

    private List<ValueLabel> values(List<CkanValueLabel> values) {
        return ofNullable(values)
                .orElseGet(List::of)
                .stream()
                .map(PackageShowMapper::value)
                .filter(Objects::nonNull)
                .toList();
    }

    private ValueLabel value(String value) {
        return ofNullable(value)
                .filter(not(String::isBlank))
                .map(it -> ValueLabel.builder()
                        .value(it)
                        .label(it)
                        .build())
                .orElse(null);
    }

    private ValueLabel value(CkanValueLabel value) {
        return ofNullable(value)
                .map(it -> ValueLabel.builder()
                        .value(value.getName())
                        .label(value.getDisplayName())
                        .build())
                .orElse(null);

    }

    private ValueLabel creator(CkanCreator creator) {
        return ValueLabel.builder()
                .label(creator.getCreatorName())
                .value(creator.getCreatorIdentifier())
                .build();
    }

    private OffsetDateTime parse(String date) {
        if (date == null) {
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

    private List<RetrievedDistribution> distributions(CkanPackage ckanPackage) {
        return ofNullable(ckanPackage.getResources())
                .orElseGet(List::of)
                .stream()
                .map(PackageShowMapper::distribution)
                .toList();
    }

    private List<ValueLabel> keywords(CkanPackage ckanPackage) {
        return ofNullable(ckanPackage.getTags())
                .orElseGet(List::of)
                .stream()
                .map(PackageShowMapper::keyword)
                .toList();
    }

    private RetrievedDistribution distribution(CkanResource ckanResource) {
        return RetrievedDistribution.builder()
                .id(ckanResource.getId())
                .title(ckanResource.getName())
                .description(ckanResource.getDescription())
                .format(value(ckanResource.getFormat()))
                .uri(ckanResource.getUri())
                .createdAt(parse(ckanResource.getCreated()))
                .modifiedAt(parse(ckanResource.getLastModified()))
                .build();
    }

    private ValueLabel keyword(CkanTag ckanTag) {
        return ValueLabel.builder()
                .label(ckanTag.getDisplayName())
                .value(ckanTag.getName())
                .build();
    }
}
