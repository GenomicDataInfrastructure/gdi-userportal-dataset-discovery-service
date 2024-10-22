// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils;

import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CkanQueryOperatorMapper {

    private final String AND = " AND ";
    private final String OR = " OR ";

    public String getOperator(DatasetSearchQuery.OperatorEnum operator) {
        return DatasetSearchQuery.OperatorEnum.AND.equals(operator) ? AND : OR;
    }
}
