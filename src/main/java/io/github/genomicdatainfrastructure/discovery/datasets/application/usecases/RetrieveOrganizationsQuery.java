// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.application.usecases;

import io.github.genomicdatainfrastructure.discovery.model.DatasetOrganization;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence.CkanOrganizationsRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.Optional.ofNullable;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RetrieveOrganizationsQuery {

    private final CkanOrganizationsRepository organizationsRepository;

    public List<DatasetOrganization> execute(Integer limit) {
        var nonNullLimit = ofNullable(limit).orElse(100);

        return organizationsRepository.findAll(nonNullLimit);
    }
}
