// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils;

import static java.util.Optional.*;

import java.util.List;
import java.util.Objects;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import lombok.experimental.UtilityClass;

// TODO review original field and date format on resources
@UtilityClass
public class PackageShowMapper {

    public RetrievedDataset from(CkanPackage ckanPackage) {
        var catalogue = ofNullable(ckanPackage.getOrganization())
                .map(CkanOrganization::getTitle)
                .orElse(null);

        return RetrievedDataset.builder()
                .id(ckanPackage.getId())
                .identifier(ckanPackage.getIdentifier())
                .title(ckanPackage.getTitle())
                .description(ckanPackage.getNotes())
                .themes(CkanValueLabelParser.values(ckanPackage.getTheme()))
                .catalogue(catalogue)
                .organization(CkanOrganizationParser.organization(ckanPackage.getOrganization()))
                .createdAt(CkanDatetimeParser.datetime(ckanPackage.getIssued()))
                .modifiedAt(CkanDatetimeParser.datetime(ckanPackage.getModified()))
                .url(ckanPackage.getUrl())
                .languages(CkanValueLabelParser.values(ckanPackage.getLanguage()))
                .creators(agents(ckanPackage.getCreator()))
                .publishers(agents(ckanPackage.getPublisher()))
                .hasVersions(CkanValueLabelParser.values(ckanPackage.getHasVersion()))
                .accessRights(CkanValueLabelParser.value(ckanPackage.getAccessRights()))
                .conformsTo(CkanValueLabelParser.values(ckanPackage.getConformsTo()))
                .provenance(ckanPackage.getProvenance())
                .spatial(CkanValueLabelParser.value(ckanPackage.getSpatialUri()))
                .distributions(distributions(ckanPackage.getResources()))
                .keywords(CkanTagParser.keywords(ckanPackage.getTags()))
                .contacts(contactPoint(ckanPackage.getContact()))
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
                .name(value.getName())
                .email(value.getEmail())
                .uri(value.getUri())
                .build();
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

    private List<RetrievedDistribution> distributions(List<CkanResource> resources) {
        return ofNullable(resources)
                .orElseGet(List::of)
                .stream()
                .map(PackageShowMapper::distribution)
                .toList();
    }

    private RetrievedDistribution distribution(CkanResource ckanResource) {
        return RetrievedDistribution.builder()
                .id(ckanResource.getId())
                .title(ckanResource.getName())
                .description(ckanResource.getDescription())
                .format(CkanValueLabelParser.value(ckanResource.getFormat()))
                .createdAt(CkanDatetimeParser.datetime(ckanResource.getIssuedDate()))
                .modifiedAt(CkanDatetimeParser.datetime(ckanResource.getModifiedDate()))
                .accessUrl(ckanResource.getAccessUrl())
                .downloadUrl(ckanResource.getDownloadUrl())
                .languages(CkanValueLabelParser.values(ckanResource.getLanguage()))
                .build();
    }

    private List<Agent> agents(List<CkanAgent> creators) {
        return ofNullable(creators)
                .orElseGet(List::of)
                .stream()
                .map(PackageShowMapper::agent)
                .filter(Objects::nonNull)
                .toList();
    }

    private Agent agent(CkanAgent value) {
        return ofNullable(value)
                .map(it -> Agent.builder()
                        .name(value.getName())
                        .email(value.getEmail())
                        .type(value.getType())
                        .identifier(value.getIdentifier())
                        .url(value.getUrl())
                        .build())
                .orElse(null);
    }
}
