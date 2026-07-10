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
     * {@code label} is left as a raw {@link JsonNode} because it accepts several shapes: a
     * single string, a list of strings, a language-code map to a single string, or a
     * language-code map to a list of strings. See {@code YamlHelpTextLoader#localizeLabel} for
     * how each shape is resolved.
     */
    public record YamlHelpTextLink(JsonNode label, List<String> value) {
    }
}
