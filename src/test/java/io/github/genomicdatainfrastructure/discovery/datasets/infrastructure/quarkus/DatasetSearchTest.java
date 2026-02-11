// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.quarkus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.containsString;

import io.github.genomicdatainfrastructure.discovery.BaseTest;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQueryFacet;
import io.github.genomicdatainfrastructure.discovery.model.FilterType;
import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Test;
import java.util.List;

@QuarkusTest
class DatasetSearchTest extends BaseTest {

    @Test
    void canAnonymouslySearchDatasets() {
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
    void canSearchDatasets_withoutBeaconFilters() {
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
    void canSearchDatasets_withBeaconFilters() {
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("beacon")
                                .type(FilterType.DROPDOWN)
                                .key("dummy")
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
    void shouldSkipSearchDatasets_whenBeaconReturnsEmptyResultSets() {
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("beacon")
                                .type(FilterType.DROPDOWN)
                                .key("dummy")
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

    @Test
    void shouldCaptureBeaconError_when401Unauthorized() {
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("beacon")
                                .type(FilterType.DROPDOWN)
                                .key("dummy")
                                .value("BEACON_ERROR_401")
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
                .body("beaconError", notNullValue())
                .body("beaconError", containsString("not recognised as a Researcher"))
                .body("count", equalTo(3)); // Falls back to CKAN results
    }

    @Test
    void shouldCaptureBeaconError_when403Forbidden() {
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("beacon")
                                .type(FilterType.DROPDOWN)
                                .key("dummy")
                                .value("BEACON_ERROR_403")
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
                .body("beaconError", notNullValue())
                .body("beaconError", containsString("not recognised as a Researcher"))
                .body("count", equalTo(3)); // Falls back to CKAN results
    }

    @Test
    void shouldCaptureBeaconError_when500InternalServerError() {
        var query = DatasetSearchQuery.builder()
                .facets(List.of(
                        DatasetSearchQueryFacet.builder()
                                .source("beacon")
                                .type(FilterType.DROPDOWN)
                                .key("dummy")
                                .value("BEACON_ERROR_500")
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
                .body("beaconError", notNullValue())
                .body("beaconError", containsString("unexpected remote exception"))
                .body("count", equalTo(3)); // Falls back to CKAN results
    }
}
