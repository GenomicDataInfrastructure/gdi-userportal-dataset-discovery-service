// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom deserializer for CKAN tags field.
 *
 * CKAN returns tags in inconsistent formats:
 * 1. As plain strings: ["tag1", "tag2"]
 * 2. As objects: [{"name": "tag1", "display_name": "Tag 1"}, ...]
 *
 * This deserializer handles both formats and extracts the tag value as a string.
 * For objects, it prefers display_name over name.
 */
public class CkanTagsDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        List<String> tags = new ArrayList<>();

        if (p.currentToken() != JsonToken.START_ARRAY) {
            return tags;
        }

        while (p.nextToken() != JsonToken.END_ARRAY) {
            String tag = deserializeTag(p);
            if (StringUtils.isNotBlank(tag)) {
                tags.add(tag.trim());
            }
        }

        return tags;
    }

    private String deserializeTag(JsonParser p) throws IOException {
        JsonToken token = p.currentToken();

        if (token == JsonToken.VALUE_STRING) {
            // Tag is a plain string
            return p.getText();
        }

        if (token == JsonToken.START_OBJECT) {
            // Tag is an object with name/display_name fields
            String name = null;
            String displayName = null;

            while (p.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = p.currentName();
                p.nextToken();

                if ("display_name".equals(fieldName)) {
                    displayName = p.getText();
                } else if ("name".equals(fieldName)) {
                    name = p.getText();
                }
                // Skip other fields
            }

            // Prefer display_name, fallback to name
            return StringUtils.isNotBlank(displayName) ? displayName : name;
        }

        // Skip unexpected tokens
        return null;
    }
}
