// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanContactPoint;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonConfigTest {

    @Test
    void shouldDeserializeSingleStringIntoCollectionField() throws Exception {
        var objectMapper = new ObjectMapper();
        new JacksonConfig().customize(objectMapper);

        var json = """
                {
                  "name": "Example contact",
                  "url": "https://www.example.com/website_contactPoint1"
                }
                """;

        var contactPoint = objectMapper.readValue(json, CkanContactPoint.class);

        assertThat(contactPoint.getUrl())
                .containsExactly("https://www.example.com/website_contactPoint1");
    }
}