// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.quarkus;

import io.github.genomicdatainfrastructure.discovery.BaseTest;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class RetrieveFiltersValuesQueryTest extends BaseTest {

    @Test
    void can_retrieve_themes() {
        var response = given()
                .when()
                .get("/api/v1/filters/theme/values");

        var actual = response.getBody()
                .as(new TypeRef<List<ValueLabel>>() {

                });

        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                        ValueLabel.builder()
                                .value("https://publications.europa.eu/resource/authority/data-theme/TECH")
                                .label("Science and technology")
                                .count(4)
                                .build(),
                        ValueLabel.builder()
                                .value("https://gdi.onemilliongenomes.eu")
                                .label("GDI")
                                .count(1)
                                .build()
                );
    }
}
