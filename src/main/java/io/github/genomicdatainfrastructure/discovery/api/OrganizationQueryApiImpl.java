// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.datasets.application.usecases.RetrieveOrganizationsQuery;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrganizationQueryApiImpl implements OrganizationQueryApi {

    private final RetrieveOrganizationsQuery organizationsQuery;

    @Override
    public Response retrieveOrganizations(Integer limit) {
        var content = organizationsQuery.execute(limit);
        return Response.ok(content).build();
    }
}
