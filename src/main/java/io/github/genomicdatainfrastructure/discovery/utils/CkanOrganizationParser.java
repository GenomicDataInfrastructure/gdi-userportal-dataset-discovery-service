// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.utils;

import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.function.Predicate;

@UtilityClass
public class CkanOrganizationParser {

    public DatasetOrganization organization(CkanOrganization organization) {
        if (organization == null) {
            return null;
        }

        return DatasetOrganization.builder()
                .id(organization.getId())
                .name(organization.getName())
                .title(nullableString(organization.getTitle()))
                .description(nullableString(organization.getDescription()))
                .imageUrl(nullableString(organization.getImageUrl()))
                .numberOfDatasets(organization.getPackageCount())
                .build();
    }

    private String nullableString(String value) {
        return Optional.ofNullable(value)
                .filter(Predicate.not(String::isBlank))
                .orElse(null);
    }
}
