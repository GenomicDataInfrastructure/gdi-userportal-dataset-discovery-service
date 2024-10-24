// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.application.ports;

import io.github.genomicdatainfrastructure.discovery.model.DatasetOrganization;

import java.util.List;

public interface OrganizationsRepository {

    List<DatasetOrganization> findAll(Integer limit);
}
