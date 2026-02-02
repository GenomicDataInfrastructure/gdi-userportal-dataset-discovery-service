// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CkanTagsDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.addMixIn(CkanPackage.class, CkanPackageMixin.class);
    }

    @Test
    void deserialize_tags_as_strings() throws JsonProcessingException {
        String json = """
                {
                    "tags": ["tag1", "tag2", "tag3"]
                }
                """;

        CkanPackage result = objectMapper.readValue(json, CkanPackage.class);

        assertThat(result.getTags()).containsExactly("tag1", "tag2", "tag3");
    }

    @Test
    void deserialize_tags_as_objects() throws JsonProcessingException {
        String json = """
                {
                    "tags": [
                        {"name": "tag-1", "display_name": "Tag 1"},
                        {"name": "tag-2", "display_name": "Tag 2"}
                    ]
                }
                """;

        CkanPackage result = objectMapper.readValue(json, CkanPackage.class);

        assertThat(result.getTags()).containsExactly("Tag 1", "Tag 2");
    }

    @Test
    void deserialize_tags_prefers_display_name_over_name() throws JsonProcessingException {
        String json = """
                {
                    "tags": [
                        {"name": "technical-name", "display_name": "Human Readable Name"}
                    ]
                }
                """;

        CkanPackage result = objectMapper.readValue(json, CkanPackage.class);

        assertThat(result.getTags()).containsExactly("Human Readable Name");
    }

    @Test
    void deserialize_tags_falls_back_to_name_when_display_name_blank() throws JsonProcessingException {
        String json = """
                {
                    "tags": [
                        {"name": "fallback-name", "display_name": ""}
                    ]
                }
                """;

        CkanPackage result = objectMapper.readValue(json, CkanPackage.class);

        assertThat(result.getTags()).containsExactly("fallback-name");
    }

    @Test
    void deserialize_tags_mixed_formats() throws JsonProcessingException {
        String json = """
                {
                    "tags": [
                        "plain-string-tag",
                        {"name": "object-tag", "display_name": "Object Tag"}
                    ]
                }
                """;

        CkanPackage result = objectMapper.readValue(json, CkanPackage.class);

        assertThat(result.getTags()).containsExactly("plain-string-tag", "Object Tag");
    }

    @Test
    void deserialize_tags_empty_array() throws JsonProcessingException {
        String json = """
                {
                    "tags": []
                }
                """;

        CkanPackage result = objectMapper.readValue(json, CkanPackage.class);

        assertThat(result.getTags()).isEmpty();
    }

    @Test
    void deserialize_tags_filters_blank_values() throws JsonProcessingException {
        String json = """
                {
                    "tags": ["  ", "", "valid-tag", "  Whole Genomes Sequencing  "]
                }
                """;

        CkanPackage result = objectMapper.readValue(json, CkanPackage.class);

        assertThat(result.getTags()).containsExactly("valid-tag", "Whole Genomes Sequencing");
    }
}
