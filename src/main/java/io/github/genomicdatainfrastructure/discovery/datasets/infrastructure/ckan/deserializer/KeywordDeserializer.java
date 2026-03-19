// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.deserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Custom deserializer for the 'keyword' field in CKAN responses. Handles both
 * array and comma-separated string formats.
 *
 * <p>
 * CKAN API may return keywords in two formats: - As an array:
 * ["cybersecurity", "images", "AIL", "darknet"] - As a comma-separated string:
 * "cybersecurity,images,AIL,darknet"
 *
 * <p>
 * This deserializer normalizes both formats to a List<String>.
 */
public class KeywordDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser parser,
            DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);

        if (node == null || node.isNull() || node.isMissingNode()) {
            return new ArrayList<>();
        }

        // Handle array format
        if (node.isArray()) {
            List<String> keywords = new ArrayList<>();
            for (JsonNode element : node) {
                if (element.isTextual()) {
                    String keyword = element.asText().trim();
                    if (!keyword.isEmpty()) {
                        keywords.add(keyword);
                    }
                }
            }
            return keywords;
        }

        // Handle string format (comma-separated)
        if (node.isTextual()) {
            String text = node.asText().trim();
            if (text.isEmpty()) {
                return new ArrayList<>();
            }

            return Arrays.stream(text.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        return new ArrayList<>();
    }

    @Override
    public List<String> getNullValue(DeserializationContext ctxt) {
        return new ArrayList<>();
    }
}
