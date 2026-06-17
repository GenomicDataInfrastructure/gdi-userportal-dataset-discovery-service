// SPDX-FileCopyrightText: 2026 Health-RI
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.ckan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFilterHelpTextsResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

class CkanFilterHelpTextServiceTest {

    @Test
    void enrichUsesRequestedFilterKeysWhenCallingCkan() {
        var ckanQueryApi = mock(CkanQueryApi.class);
        var service = new CkanFilterHelpTextService(ckanQueryApi, new ObjectMapper());
        var capturedKeys = new String[1];

        when(ckanQueryApi.gdiFilterHelpTextsShow(anyString(), anyString())).thenAnswer(
                invocation -> {
                    capturedKeys[0] = invocation.getArgument(1, String.class);
                    return CkanFilterHelpTextsResponse.builder()
                            .result(Map.of())
                            .build();
                });

        var filters = List.of(
                Filter.builder().key("title").build(),
                Filter.builder().key("theme").build()
        );

        service.enrich(filters, "en");

        assertThat(capturedKeys[0])
                .isEqualTo("[\"title\",\"theme\"]");
    }

    @Test
    void enrichNormalizesMultilineHelpTextValues() {
        var ckanQueryApi = mock(CkanQueryApi.class);
        var service = new CkanFilterHelpTextService(ckanQueryApi, new ObjectMapper());

        when(ckanQueryApi.gdiFilterHelpTextsShow(anyString(), anyString())).thenReturn(
                CkanFilterHelpTextsResponse.builder()
                        .result(Map.of(
                                "health_theme",
                                "A category of the Dataset or tag describing the Dataset.\n",
                                "access_rights",
                                "Information that indicates whether\nthis dataset is open or restricted."
                        ))
                        .build());

        var filters = List.of(
                Filter.builder().key("health_theme").build(),
                Filter.builder().key("access_rights").build()
        );

        service.enrich(filters, "en");

        assertThat(filters.get(0).getHelpText())
                .isEqualTo("A category of the Dataset or tag describing the Dataset.");
        assertThat(filters.get(1).getHelpText())
                .isEqualTo(
                        "Information that indicates whether this dataset is open or restricted.");
    }

    @Test
    void enrichSkipsNullHelpTextValues() {
        var ckanQueryApi = mock(CkanQueryApi.class);
        var service = new CkanFilterHelpTextService(ckanQueryApi, new ObjectMapper());

        var result = new LinkedHashMap<String, String>();
        result.put("title", null);

        when(ckanQueryApi.gdiFilterHelpTextsShow(anyString(), anyString())).thenReturn(
                CkanFilterHelpTextsResponse.builder()
                        .result(result)
                        .build());

        var filters = List.of(Filter.builder().key("title").build());

        service.enrich(filters, "en");

        assertThat(filters.get(0).getHelpText()).isNull();
    }
}
