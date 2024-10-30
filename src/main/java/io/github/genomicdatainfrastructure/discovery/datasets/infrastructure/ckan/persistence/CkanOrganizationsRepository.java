// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.OrganizationsRepository;
import io.github.genomicdatainfrastructure.discovery.model.DatasetOrganization;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils.CkanOrganizationParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class CkanOrganizationsRepository implements OrganizationsRepository {

    private static final Boolean SHOW_ALL_FIELDS = true;

    private final CkanQueryApi ckanQueryApi;

    @Inject
    public CkanOrganizationsRepository(
            @RestClient CkanQueryApi ckanQueryApi
    ) {
        this.ckanQueryApi = ckanQueryApi;
    }

    public List<DatasetOrganization> findAll(Integer limit) {
        return ckanQueryApi
                .organizationList(SHOW_ALL_FIELDS, limit)
                .getResult()
                .stream()
                .map(CkanOrganizationParser::organization)
                .toList();
    }
}
