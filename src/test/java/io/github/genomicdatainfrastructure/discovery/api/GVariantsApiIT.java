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
                .body("$", hasSize(2))

                // 3) Verify the first element
                .body("[0].beacon", equalTo("org.nbis.ga4gh-approval-beacon-test"))
                .body("[0].population", equalTo("FR_M"))
                .body("[0].sex", equalTo("M"))
                .body("[0].countryOfBirth", equalTo("FR"))
                .body("[0].alleleCount", equalTo(7102.0F))
                .body("[0].alleleNumber", equalTo(54233.0F))
                .body("[0].alleleCountHomozygous", equalTo(2400.0F))
                .body("[0].alleleCountHeterozygous", equalTo(4702.0F))
                .body("[0].alleleCountHemizygous", equalTo(0.0F))
                .body("[0].alleleFrequency", equalTo(0.13095F))

                // 4) Verify the second element
                .body("[1].beacon", equalTo("pt.biodata.gdi.beacon-alleles"))
                .body("[1].population", equalTo("FR_F"))
                .body("[1].sex", equalTo("F"))
                .body("[1].countryOfBirth", equalTo("FR"))
                .body("[1].alleleCount", equalTo(478.0F))
                .body("[1].alleleNumber", equalTo(30153.0F))
                .body("[1].alleleCountHomozygous", equalTo(20.0F))
                .body("[1].alleleCountHeterozygous", equalTo(458.0F))
                .body("[1].alleleCountHemizygous", equalTo(0.0F))
                .body("[1].alleleFrequency", equalTo(0.01588F));

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
                .body("$", hasSize(2))
                // Verify population extraction from GoE format works without authentication
                .body("[0].population", equalTo("FR_M"))
                .body("[0].sex", equalTo("M"))
                .body("[0].countryOfBirth", equalTo("FR"))
                .body("[1].population", equalTo("FR_F"))
                .body("[1].sex", equalTo("F"))
                .body("[1].countryOfBirth", equalTo("FR"));

    }

    @Test
    void givenCombinedFilters_whenSearchGenomicVariants_thenReturnsExactMatch() {
        GVariantSearchQuery query = buildQueryWithFilters("FR", "F");
        given()
                .contentType(JSON)
                .body(query)
                .when()
                .post("/api/v1/g_variants")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].countryOfBirth", equalTo("FR"))
                .body("[0].sex", equalTo("F"));
    }

    private static @NotNull GVariantSearchQuery buildQuery() {
        return buildQueryWithFilters(null, null);
    }

    private static @NotNull GVariantSearchQuery buildQueryWithFilters(String countryOfBirth, String sex) {
        GVariantSearchQuery query = new GVariantSearchQuery();
        GVariantSearchQueryParams params = new GVariantSearchQueryParams();
        params.setVariant("3:45864731:T:C");
        params.setRefGenome("GRCh37");
        params.setCountryOfBirth(countryOfBirth);
        params.setSex(sex);
        query.setParams(params);
        return query;
    }

}