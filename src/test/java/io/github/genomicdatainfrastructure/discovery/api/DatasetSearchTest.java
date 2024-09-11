// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.github.genomicdatainfrastructure.discovery.BaseTest;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;
import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Test;
import java.util.List;
import com.github.tomakehurst.wiremock.client.WireMock;

@QuarkusTest
class DatasetSearchTest extends BaseTest {

    WireMock wireMock;

    @Test
    void can_anonymously_search_datasets() {
        var query = DatasetSearchQuery.builder().build();

        given()
                .contentType("application/json")
                .body(query)
                .when()
                .post("/api/v1/datasets/search")
                .then()
                .statusCode(200)
                .body("count", equalTo(3))
                .body("results[0].identifier", equalTo("27866022694497975"))
                .body("results[1].identifier", equalTo("euc_kauno_uc6"))
                .body("results[2].identifier", equalTo("cp-tavi"));
    }

    @Test
    void can_search_datasets_without_beacon_filters() {
        var query = DatasetSearchQuery.builder()
                .build();

        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body(query)
                .when()
                .post("/api/v1/datasets/search")
                .then()
                .statusCode(200)
                .body("count", equalTo(3))
                .body("results[0].identifier", equalTo("27866022694497975"))
                .body("results[1].identifier", equalTo("euc_kauno_uc6"))
                .body("results[2].identifier", equalTo("cp-tavi"));
    }

    @Test
    void can_search_datasets_with_beacon_filters() {
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .facetGroup("beacon")
                                .facet("dummy")
                                .value("true")
                                .build()
                ))
                .build();

        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body(query)
                .when()
                .post("/api/v1/datasets/search")
                .then()
                .statusCode(200)
                .body("count", equalTo(1))
                .body("results[0].identifier", equalTo("27866022694497975"))
                .body("results[0].recordsCount", equalTo(64));
    }

    @Test
    void skip_search_datasets_when_beacon_returns_empty_resultsets() {
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .facetGroup("beacon")
                                .facet("dummy")
                                .value("DUMMY:FILTER")
                                .build()
                ))
                .build();
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body(query)
                .when()
                .post("/api/v1/datasets/search")
                .then()
                .statusCode(200)
                .body("count", equalTo(0));
    }

}
