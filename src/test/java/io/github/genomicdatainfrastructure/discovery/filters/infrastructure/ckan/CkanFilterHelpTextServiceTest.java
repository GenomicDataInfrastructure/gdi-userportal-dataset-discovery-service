// SPDX-FileCopyrightText: 2026 Health-RI
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.ckan;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFilterHelpTextsResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackageSearchRequest;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResponse;
import jakarta.enterprise.inject.Vetoed;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class CkanFilterHelpTextServiceTest {

    @Test
    void enrichUsesRequestedFilterKeysWhenCallingCkan() {
        var ckanQueryApi = new StubCkanQueryApi();
        var service = new CkanFilterHelpTextService(ckanQueryApi, new ObjectMapper());

        var filters = List.of(
                Filter.builder().key("title").build(),
                Filter.builder().key("theme").build()
        );

        service.enrich(filters, "en");

        assertThat(ckanQueryApi.lastKeys)
                .isEqualTo("[\"title\",\"theme\"]");
    }

    @Vetoed
    private static final class StubCkanQueryApi implements CkanQueryApi {

        private String lastKeys;

        @Override
        public PackagesSearchResponse packageSearch(String acceptLanguage,
                PackageSearchRequest packageSearchRequest) {
            throw new UnsupportedOperationException();
        }

        @Override
        public io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackageShowResponse packageShow(
                String id, String acceptLanguage) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CkanFilterHelpTextsResponse gdiFilterHelpTextsShow(String acceptLanguage,
                String keys) {
            this.lastKeys = keys;
            return CkanFilterHelpTextsResponse.builder()
                    .result(Map.of())
                    .build();
        }

        @Override
        public String retrieveDatasetInFormat(String id, String format, String authorization) {
            throw new UnsupportedOperationException();
        }
    }
}
