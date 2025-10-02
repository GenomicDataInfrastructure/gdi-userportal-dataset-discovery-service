// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.quarkus;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.discovery.BaseTest;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class RetrieveFiltersTest extends BaseTest {

    @Test
    void shouldRetrieveCkanFilters_WhenNotAuthenticated() {
        given()
                .get("/api/v1/filters")
                .then()
                .statusCode(200)
                .body("", hasSize(6))
                .body("[0].source", Matchers.equalTo("ckan"))
                .body("[0].key", Matchers.equalTo("tags"))
                .body("[0].label", Matchers.equalTo("Keywords"))
                .body("[0].values", hasSize(16))

                .body("[1].source", Matchers.equalTo("ckan"))
                .body("[1].key", Matchers.equalTo("organization"))
                .body("[1].label", Matchers.equalTo("Publishers"))
                .body("[1].values", hasSize(7))
                .body("find { it.key == 'metadata_modified' }.source", equalTo("ckan"))
                .body("find { it.key == 'metadata_modified' }.type", equalTo("DATETIME"))
                .body("find { it.key == 'metadata_modified' }.label", equalTo("metadata_modified"))
                .body("find { it.key == 'metadata_modified' }.group", equalTo("DEFAULT"))
                .body("find { it.key == 'metadata_modified' }.entries", nullValue());
    }

    @Test
    void shouldRetrieveAllFilters_WhenAuthenticated() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .get("/api/v1/filters")
                .then()
                .statusCode(200)
                .body("find { it.source == 'ckan' }.values", hasSize(greaterThan(0)))
                .body("find { it.source == 'beacon' }.values", hasSize(greaterThan(0)));
    }

    @Test
    void shouldRetrievesBeaconFilteringTerms_WhenAuthenticated() {
        var response = given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .get("/api/v1/filters");

        var mapper = new ObjectMapper();

        try {
            List<Filter> body = mapper.readerForListOf(Filter.class).readValue(response.getBody()
                    .asString());

            var actual = body
                    .stream()
                    .filter(it -> "beacon".equals(it.getSource()))
                    .map(it -> it.getKey())
                    .collect(Collectors.toSet());

            assertThat(actual)
                    .containsExactlyInAnyOrder(
                            "treatment", "genetic_variation", "sex", "histopathology", "diseases",
                            "intervention",
                            "diseases.ageOfOnset.iso8601duration", "genetic_mutation"
                    );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
