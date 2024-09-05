// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.genomicdatainfrastructure.discovery.BaseTest;
import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

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
                .body("", hasSize(11));
    }
}
