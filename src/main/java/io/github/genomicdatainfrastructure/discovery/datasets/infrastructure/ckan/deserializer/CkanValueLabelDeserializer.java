// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanValueLabel;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Handles CKAN value-label fields provided either as objects or plain strings.
 */
public class CkanValueLabelDeserializer extends JsonDeserializer<CkanValueLabel> {

    @Override
    public CkanValueLabel deserialize(JsonParser parser,
            DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);

        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }

        if (node.isTextual()) {
            return fromText(node.asText());
        }

        if (!node.isObject()) {
            return null;
        }

        var name = textOrNull(node.get("name"));
        var displayName = firstNotBlank(
                textOrNull(node.get("display_name")),
                textOrNull(node.get("displayName")),
                name);
        var count = integerOrNull(node.get("count"));

        return CkanValueLabel.builder()
                .name(name)
                .displayName(displayName)
                .count(count)
                .build();
    }

    private static CkanValueLabel fromText(String value) {
        var normalizedValue = StringUtils.trimToNull(value);
        if (normalizedValue == null) {
            return null;
        }

        return CkanValueLabel.builder()
                .name(normalizedValue)
                .displayName(normalizedValue)
                .build();
    }

    private static String textOrNull(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return StringUtils.trimToNull(node.asText());
    }

    private static Integer integerOrNull(JsonNode node) {
        if (node == null || node.isNull() || !node.canConvertToInt()) {
            return null;
        }
        return node.asInt();
    }

    private static String firstNotBlank(String... values) {
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }
}
