// SPDX-FileCopyrightText: 2026 Health-RI
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.helptext.infrastructure;

import io.github.genomicdatainfrastructure.discovery.model.HelpText;
import io.github.genomicdatainfrastructure.discovery.model.HelpTextLink;

import java.util.List;

/**
 * Builds {@link HelpText} for CKAN-sourced text, which never has link data. {@code link} is still
 * always populated (never {@code null}) so the API response shape matches the YAML-sourced path.
 */
public final class HelpTexts {

    private HelpTexts() {
    }

    public static HelpText textOnly(String text) {
        if (text == null) {
            return null;
        }

        return HelpText.builder()
                .text(text)
                .link(HelpTextLink.builder().label(List.of()).value(List.of()).build())
                .build();
    }
}
