// SPDX-FileCopyrightText: 2026 Health-RI
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.helptext.infrastructure.yaml;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Raw shape of a single help-text entry as parsed from a help-text YAML source file, before
 * resolving it down to a single requested language. {@code text} is keyed by language code (e.g.
 * {@code "en"}, {@code "nl"}).
 */
public record YamlHelpTextEntry(Map<String, String> text, YamlHelpTextLink link) {

    /**
     * {@code label} is keyed by language code, and each language's value may be either a single
     * string or a list of strings (kept as a {@link JsonNode} so both are accepted). See
     * {@code YamlHelpTextLoader#localizeLabel} for how it is resolved.
     */
    public record YamlHelpTextLink(Map<String, JsonNode> label, List<String> value) {
    }
}
