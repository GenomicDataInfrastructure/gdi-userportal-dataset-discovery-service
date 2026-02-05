// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import org.junit.jupiter.api.Test;

import io.github.genomicdatainfrastructure.discovery.BaseTest;
import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class RetrieveDatasetTest extends BaseTest {

    @Test
    void can_anonymously_retrieve_dataset() {
        given()
                .when()
                .get("/api/v1/datasets/e1b3eff9-13eb-48b0-b180-7ecb76b84454")
                .then()
                .statusCode(200);
    }

    @Test
    void can_retrieve_dataset() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .get("/api/v1/datasets/e1b3eff9-13eb-48b0-b180-7ecb76b84454")
                .then()
                .statusCode(200);
    }

    @Test
    void retrieves_404_when_not_found() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .get("/api/v1/datasets/dummy")
                .then()
                .statusCode(404)
                .body("title", equalTo("Dataset Not Found"))
                .body("status", equalTo(404))
                .body("detail", equalTo("Dataset dummy not found"));
    }

    @Test
    void retrieves_500_when_not_expected_status_code() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .get("/api/v1/datasets/dataset_with_error")
                .then()
                .statusCode(500)
                .body("title", equalTo("Not expected exception"))
                .body("status", equalTo(500))
                .body("detail", equalTo(
                        "Received: 'Server Error, status code 500' when invoking REST Client method: 'io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi#packageShow'"
                ));
    }

    @Test
    void can_anonymously_retrieve_dataset_in_format() {
        given()
                .when()
                .get("/api/v1/datasets/e1b3eff9-13eb-48b0-b180-7ecb76b84454.jsonld")
                .then()
                .statusCode(200);
    }

    @Test
    void can_retrieve_dataset_in_format() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .get("/api/v1/datasets/e1b3eff9-13eb-48b0-b180-7ecb76b84454.jsonld")
                .then()
                .statusCode(200);
    }

    @Test
    void retrieves_404_when_not_found_in_format() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .get("/api/v1/datasets/dummy.jsonld")
                .then()
                .statusCode(404)
                .body("title", equalTo("Dataset Not Found"))
                .body("status", equalTo(404))
                .body("detail", equalTo("Dataset dummy not found"));
    }

    @Test
    void retrieves_500_when_not_expected_status_code_in_format() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .get("/api/v1/datasets/dataset_with_error.jsonld")
                .then()
                .statusCode(500)
                .body("title", equalTo("Not expected exception"))
                .body("status", equalTo(500))
                .body("detail", equalTo(
                        "Received: 'Server Error, status code 500' when invoking REST Client method: 'io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi#retrieveDatasetInFormat'"
                ));
    }

    @Test
    void retrieves_400_when_not_expected_file_format() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .get("/api/v1/datasets/dataset_with_error.dummy")
                .then()
                .statusCode(400)
                .body("title", equalTo("Not expected argument"))
                .body("status", equalTo(400))
                .body("detail", equalTo("Unsupported format: dummy"));
    }
}
