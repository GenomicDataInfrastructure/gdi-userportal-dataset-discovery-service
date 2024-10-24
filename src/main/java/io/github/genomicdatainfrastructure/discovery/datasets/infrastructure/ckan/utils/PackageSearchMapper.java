// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils;

import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

import static java.util.Optional.ofNullable;

@UtilityClass
public class PackageSearchMapper {

    public List<SearchedDataset> from(PackagesSearchResult result) {
        var nonNullPackages = ofNullable(result)
                .map(PackagesSearchResult::getResults)
                .filter(ObjectUtils::isNotEmpty)
                .orElseGet(List::of);

        return nonNullPackages.stream()
                .map(PackageSearchMapper::result)
                .toList();
    }

    private SearchedDataset result(CkanPackage ckanPackage) {
        var catalogue = ofNullable(ckanPackage.getOrganization())
                .map(CkanOrganization::getTitle)
                .orElse(null);

        return SearchedDataset.builder()
                .id(ckanPackage.getId())
                .identifier(ckanPackage.getIdentifier())
                .title(ckanPackage.getTitle())
                .description(ckanPackage.getNotes())
                .themes(CkanValueLabelParser.values(ckanPackage.getTheme()))
                .keywords(CkanTagParser.keywords(ckanPackage.getTags()))
                .catalogue(catalogue)
                .organization(CkanOrganizationParser.organization(ckanPackage.getOrganization()))
                .modifiedAt(CkanDatetimeParser.datetime(ckanPackage.getModified()))
                .createdAt(CkanDatetimeParser.datetime(ckanPackage.getIssued()))
                .distributionsCount(ckanPackage.getResources().size())
                .build();
    }
}
