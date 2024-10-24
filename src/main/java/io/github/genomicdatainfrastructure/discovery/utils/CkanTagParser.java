// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.utils;

import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanTag;
import lombok.experimental.UtilityClass;

import java.util.List;

import static java.util.Optional.ofNullable;

@UtilityClass
public class CkanTagParser {

    public List<ValueLabel> keywords(List<CkanTag> tags) {
        return ofNullable(tags)
                .orElseGet(List::of)
                .stream()
                .map(CkanTagParser::keyword)
                .toList();
    }

    private ValueLabel keyword(CkanTag ckanTag) {
        return ValueLabel.builder()
                .label(ckanTag.getDisplayName())
                .value(ckanTag.getName())
                .build();
    }
}
