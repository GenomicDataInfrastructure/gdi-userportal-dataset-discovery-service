// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.deserializer;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KeywordDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    static class TestObject {

        @JsonDeserialize(using = KeywordDeserializer.class)
        public List<String> keyword;
    }

    @Test
    void deserialize_whenArrayFormat_shouldReturnList() throws JsonProcessingException {
        String json = "{\"keyword\": [\"cybersecurity\", \"images\", \"AIL\", \"darknet\"]}";

        TestObject result = objectMapper.readValue(json, TestObject.class);

        assertThat(result.keyword).containsExactly("cybersecurity", "images", "AIL", "darknet");
    }

    @Test
    void deserialize_whenCommaSeparatedString_shouldReturnList() throws JsonProcessingException {
        String json = "{\"keyword\": \"cybersecurity,images,AIL,darknet\"}";

        TestObject result = objectMapper.readValue(json, TestObject.class);

        assertThat(result.keyword).containsExactly("cybersecurity", "images", "AIL", "darknet");
    }

    @Test
    void deserialize_whenEmptyString_shouldReturnEmptyList() throws JsonProcessingException {
        String json = "{\"keyword\": \"\"}";

        TestObject result = objectMapper.readValue(json, TestObject.class);

        assertThat(result.keyword).isEmpty();
    }

    @Test
    void deserialize_whenEmptyArray_shouldReturnEmptyList() throws JsonProcessingException {
        String json = "{\"keyword\": []}";

        TestObject result = objectMapper.readValue(json, TestObject.class);

        assertThat(result.keyword).isEmpty();
    }

    @Test
    void deserialize_whenNull_shouldReturnEmptyList() throws JsonProcessingException {
        String json = "{\"keyword\": null}";

        TestObject result = objectMapper.readValue(json, TestObject.class);

        assertThat(result.keyword).isNotNull().isEmpty();
    }

    @Test
    void deserialize_whenStringWithSpaces_shouldTrimValues() throws JsonProcessingException {
        String json = "{\"keyword\": \" cybersecurity , images , AIL \"}";

        TestObject result = objectMapper.readValue(json, TestObject.class);

        assertThat(result.keyword).containsExactly("cybersecurity", "images", "AIL");
    }

    @Test
    void deserialize_whenArrayWithEmptyStrings_shouldFilterThem() throws JsonProcessingException {
        String json = "{\"keyword\": [\"cybersecurity\", \"\", \"images\", \"  \"]}";

        TestObject result = objectMapper.readValue(json, TestObject.class);

        assertThat(result.keyword).containsExactly("cybersecurity", "images");
    }

    @Test
    void deserialize_whenSingleValue_shouldReturnSingleElementList() throws JsonProcessingException {
        String json = "{\"keyword\": \"cybersecurity\"}";

        TestObject result = objectMapper.readValue(json, TestObject.class);

        assertThat(result.keyword).containsExactly("cybersecurity");
    }
}
