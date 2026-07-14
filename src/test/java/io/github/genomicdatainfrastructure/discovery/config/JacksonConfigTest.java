// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQueryFilter;
import org.junit.jupiter.api.Test;

import static io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQueryFilter.OperatorEnum.GREATER_THAN_SYMBOL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JacksonConfigTest {

    private final JacksonConfig jacksonConfig = new JacksonConfig();

    @Test
    void shouldOmitNullOperatorAndValue_WhenSerializingDropdownBeaconFilter() throws Exception {
        var mapper = new ObjectMapper();
        jacksonConfig.customize(mapper);

        var filter = BeaconRequestQueryFilter.builder()
                .id("NCIT:C16576")
                .scope("individual")
                .build();

        var json = mapper.writeValueAsString(filter);

        assertThat(json).contains("\"id\":\"NCIT:C16576\"");
        assertThat(json).contains("\"scope\":\"individual\"");
        assertThat(json).doesNotContain("operator");
        assertThat(json).doesNotContain("value");
    }

    @Test
    void shouldKeepOperatorAndValue_WhenSerializingAlphanumericBeaconFilter() throws Exception {
        var mapper = new ObjectMapper();
        jacksonConfig.customize(mapper);

        var filter = BeaconRequestQueryFilter.builder()
                .id("ageOfOnset")
                .scope("individual")
                .operator(GREATER_THAN_SYMBOL)
                .value("20")
                .build();

        var json = mapper.writeValueAsString(filter);

        assertThat(json).contains("\"operator\":\">\"");
        assertThat(json).contains("\"value\":\"20\"");
    }
}
