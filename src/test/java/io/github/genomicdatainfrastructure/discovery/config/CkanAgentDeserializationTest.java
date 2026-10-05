// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Agents used to carry a single country and identifier. CKAN may still return those stored
 * values as scalars, so the application mapper has to read both shapes as lists.
 */
class CkanAgentDeserializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        new JacksonConfig().customize(mapper);
    }

    private CkanAgent read(String json) throws JsonProcessingException {
        return mapper.readValue(json, CkanAgent.class);
    }

    @Test
    void identifier_legacyScalarIsReadAsOneValue() throws JsonProcessingException {
        assertThat(read("{\"name\": \"Org\", \"identifier\": \"https://ror.org/05sk8w809\"}")
                .getIdentifier()).containsExactly("https://ror.org/05sk8w809");
    }

    @Test
    void identifier_scalarIsNotSplitOnCommas() throws JsonProcessingException {
        assertThat(read("{\"identifier\": \"doi:10.1000/a,b\"}").getIdentifier())
                .containsExactly("doi:10.1000/a,b");
    }

    @Test
    void identifier_arrayKeepsAllValues() throws JsonProcessingException {
        assertThat(read("{\"identifier\": [\"https://orcid.org/0000-0001\", \"id-2\"]}")
                .getIdentifier()).containsExactly("https://orcid.org/0000-0001", "id-2");
    }

    @Test
    void country_legacySingleObjectIsReadAsOneValue() throws JsonProcessingException {
        var country = read(
                "{\"country\": {\"name\": \"http://x/NLD\", \"display_name\": \"Netherlands\"}}")
                .getCountry();

        assertThat(country).hasSize(1);
        assertThat(country.get(0).getName()).isEqualTo("http://x/NLD");
        assertThat(country.get(0).getDisplayName()).isEqualTo("Netherlands");
    }

    @Test
    void country_legacyRawUriIsReadAsOneValue() throws JsonProcessingException {
        var country = read("{\"country\": \"http://x/NLD\"}").getCountry();

        assertThat(country).hasSize(1);
        assertThat(country.get(0).getName()).isEqualTo("http://x/NLD");
    }

    @Test
    void country_arrayKeepsAllValues() throws JsonProcessingException {
        var country = read(
                "{\"country\": [{\"name\": \"http://x/NLD\"}, {\"name\": \"http://x/DEU\"}]}")
                .getCountry();

        assertThat(country).extracting("name").containsExactly("http://x/NLD", "http://x/DEU");
    }

    @Test
    void missingCountryAndIdentifierStayNull() throws JsonProcessingException {
        var agent = read("{\"name\": \"Org\"}");

        assertThat(agent.getCountry()).isNull();
        assertThat(agent.getIdentifier()).isNull();
    }
}
