// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.BaseTest;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQueryParams;
import io.quarkus.test.junit.QuarkusTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

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
                .body("numberOfPopulations", equalTo(2))
                .body("source", equalTo("The Genome of Europe"))
                .body("sourceReference", equalTo("https://genomeofeurope.eu/"))
                .body("populations", hasSize(2))

                // 3) Verify the first population element
                .body("populations[0].population", equalTo("FIN"))
                .body("populations[0].alleleCount", equalTo("491"))
                .body("populations[0].alleleNumber", equalTo("82998"))
                .body("populations[0].alleleCountHomozygous", equalTo("8"))
                .body("populations[0].alleleCountHeterozygous", equalTo("483"))
                .body("populations[0].alleleFrequency", equalTo("0.005915809888392687"))

                // 4) Verify the second population element
                .body("populations[1].population", equalTo("ITA"))
                .body("populations[1].alleleCount", equalTo("478"))
                .body("populations[1].alleleNumber", equalTo("83028"))
                .body("populations[1].alleleCountHomozygous", equalTo("4"))
                .body("populations[1].alleleCountHeterozygous", equalTo("474"))
                .body("populations[1].alleleFrequency", equalTo("0.004268940072506666"));

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
                .body("numberOfPopulations", equalTo(0))
                .body("populations", hasSize(0));

    }

    private static @NotNull GVariantSearchQuery buildQuery() {
        GVariantSearchQuery query = new GVariantSearchQuery();
        GVariantSearchQueryParams params = new GVariantSearchQueryParams();
        params.put("alternateBases", "C");
        params.put("referenceBases", "T");
        params.put("start", new int[]{45864731});
        params.put("referenceName", "3");
        params.put("assemblyId", "GRCh37");
        query.setParams(params);
        return query;
    }

}