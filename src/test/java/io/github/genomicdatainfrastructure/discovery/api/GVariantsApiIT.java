// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.BaseTest;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import io.quarkus.test.junit.QuarkusTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
class GVariantsApiIT extends BaseTest {

    @Test
    void givenValidGVariantSearchQueryAndAuthorisedUser_and_whenSearchGenomicVariants_thenReturnOkResponse() {
        GVariantSearchQuery query = buildQuery();
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType(JSON)
                .body(query)
                .when()
                .post("/api/v1/g_variants")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))

                // 3) Verify the first element
                .body("[0].beacon", equalTo("org.nbis.ga4gh-approval-beacon-test"))
                .body("[0].population", equalTo("fin"))
                .body("[0].alleleCount", equalTo(491.0F))
                .body("[0].alleleNumber", equalTo(82998.0F))
                .body("[0].alleleCountHomozygous", equalTo(8.0F))
                .body("[0].alleleCountHeterozygous", equalTo(483.0F))
                .body("[0].alleleFrequency", equalTo(0.005915809888392687F))

                // 4) Verify the second element
                .body("[1].beacon", equalTo("pt.biodata.gdi.beacon-alleles"))
                .body("[1].population", equalTo("ita"))
                .body("[1].alleleCount", equalTo(478.0F))
                .body("[1].alleleNumber", equalTo(83028.0F))
                .body("[1].alleleCountHomozygous", equalTo(4))
                .body("[1].alleleCountHeterozygous", equalTo(474))
                .body("[1].alleleFrequency", equalTo(0.004268940072506666F));

    }

    @Test
    void givenValidGVariantSearchQuery_and_whenSearchGenomicVariants_thenReturnOkResponse() {
        GVariantSearchQuery query = buildQuery();
        given()
                .contentType(JSON)
                .body(query)
                .when()
                .post("/api/v1/g_variants")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));

    }

    private static @NotNull GVariantSearchQuery buildQuery() {
        GVariantSearchQuery query = new GVariantSearchQuery();
        Map<String, Object> params = new HashMap<>();
        params.put("alternateBases", "C");
        params.put("referenceBases", "T");
        params.put("start", new int[] {45864731});
        params.put("referenceName", "3");
        params.put("assemblyId", "GRCh37");
        query.setParams(params);
        return query;
    }

}