// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.utils;

import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanValueLabel;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;

import static java.util.Optional.ofNullable;

@UtilityClass
public class CkanValueLabelParser {

    public List<ValueLabel> values(List<CkanValueLabel> values) {
        return ofNullable(values)
                .orElseGet(List::of)
                .stream()
                .map(CkanValueLabelParser::value)
                .filter(Objects::nonNull)
                .toList();
    }

    public ValueLabel value(CkanValueLabel value) {
        return ofNullable(value)
                .map(it -> ValueLabel.builder()
                        .value(value.getName())
                        .label(value.getDisplayName())
                        .build())
                .orElse(null);

    }
}
