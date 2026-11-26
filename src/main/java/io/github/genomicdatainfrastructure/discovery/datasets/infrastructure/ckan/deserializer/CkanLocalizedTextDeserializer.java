// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanLocalizedText;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Reads a text that CKAN sends either as a plain string or as a text per language, for example
 * {@code {"en": "Title", "nl": "Titel"}}. CKAN resolves most multilingual fields to the language of
 * the request, but a field of a schema that is not marked multilingual can still hold a language
 * map, and a text typed without a language is a plain string. Takes English first, then the first
 * language that has a value.
 */
public class CkanLocalizedTextDeserializer extends JsonDeserializer<CkanLocalizedText> {

    private static final String PREFERRED_LANGUAGE = "en";

    @Override
    public CkanLocalizedText deserialize(JsonParser parser,
            DeserializationContext context) throws IOException {
        var text = firstText(parser.getCodec().readTree(parser));

        if (text == null) {
            return null;
        }

        return CkanLocalizedText.builder()
                .text(text)
                .build();
    }

    /**
     * Returns the first non-blank text of a string, a language map or a list, English first, or
     * {@code null} when there is none.
     */
    public static String firstText(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }

        if (node.isValueNode()) {
            return StringUtils.trimToNull(node.asText());
        }

        if (node.isObject()) {
            var preferred = firstText(node.get(PREFERRED_LANGUAGE));
            if (preferred != null) {
                return preferred;
            }
        }

        for (JsonNode child : node) {
            var text = firstText(child);
            if (text != null) {
                return text;
            }
        }

        return null;
    }
}
