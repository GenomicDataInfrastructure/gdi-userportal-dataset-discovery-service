// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.genomicdatainfrastructure.discovery.BaseTest;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.model.Facet;
import io.github.genomicdatainfrastructure.discovery.model.FacetGroup;
import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class RetrieveFacetsTest extends BaseTest {

    WireMock wireMock;

    @Test
    void retrieve_ckan_facets_when_not_authenticated() {
        given()
                .get("/api/v1/search-facets")
                .then()
                .statusCode(200)
                .body("", hasSize(5))
                .body("[0].facetGroup", Matchers.equalTo("ckan"))
                .body("[0].key", Matchers.equalTo("tags"))
                .body("[0].label", Matchers.equalTo("Keywords"))
                .body("[0].values", hasSize(16))

                .body("[1].facetGroup", Matchers.equalTo("ckan"))
                .body("[1].key", Matchers.equalTo("organization"))
                .body("[1].label", Matchers.equalTo("Publishers"))
                .body("[1].values", hasSize(7));
    }

    @Test
    void retrieve_all_facets_when_authenticated() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .get("/api/v1/search-facets")
                .then()
                .statusCode(200)
                .body("", hasSize(11))
                .body("find { it.facetGroup == 'ckan' }.values", hasSize(greaterThan(0)))
                .body("find { it.facetGroup == 'beacon' }.values", hasSize(greaterThan(0)))
                .body("find { it.label == 'Human Phenotype Ontology' }.values", hasSize(greaterThan(
                        0)));
    }

    @Test
    void retrieves_beacon_filtering_terms() {
        var response = given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .get("/api/v1/search-facets");

        var mapper = new ObjectMapper();

        try {
            List<Facet> body = mapper.readerForListOf(Facet.class).readValue(response.getBody()
                    .asString());

            var actual = body
                    .stream()
                    .filter(it -> "beacon".equals(it.getFacetGroup()))
                    .map(it -> it.getKey())
                    .collect(toList());

            assertThat(actual)
                    .containsExactlyInAnyOrder(
                            "gaz", "ncit", "opcs4", "hp", "loinc", "icd10"
                    );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}