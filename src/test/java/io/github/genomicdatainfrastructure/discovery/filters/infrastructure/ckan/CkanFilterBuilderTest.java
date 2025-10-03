// SPDX-FileCopyrightText: 2025 Health-RI
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.filters.infrastructure.ckan;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.genomicdatainfrastructure.discovery.filters.infrastructure.quarkus.DatasetsConfig;
import io.github.genomicdatainfrastructure.discovery.model.Filter;
import io.github.genomicdatainfrastructure.discovery.model.FilterType;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFacet;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackageSearchRequest;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.PackagesSearchResult;
import jakarta.enterprise.inject.Vetoed;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CkanFilterBuilderTest {

    private final TestDatasetsConfig datasetsConfig = new TestDatasetsConfig();

    @Test
    void buildsRangeMetadataForDateTimeAndNumberFacets() {
        var response = PackagesSearchResponse.builder()
                .result(PackagesSearchResult.builder()
                        .searchFacets(Map.of(
                                "modified", CkanFacet.builder()
                                        .title("Modification date")
                                        .items(List.of(
                                                CkanValueLabel.builder()
                                                        .name("2024-01-01T00:00:00+00:00")
                                                        .displayName("from")
                                                        .build(),
                                                CkanValueLabel.builder()
                                                        .name("2024-12-31T23:59:59+00:00")
                                                        .displayName("to")
                                                        .build()))
                                        .build(),
                                "number_of_records", CkanFacet.builder()
                                        .title("Number of records")
                                        .items(List.of(
                                                CkanValueLabel.builder()
                                                        .name("10")
                                                        .displayName("10")
                                                        .build(),
                                                CkanValueLabel.builder()
                                                        .name("250")
                                                        .displayName("250")
                                                        .build()))
                                        .build(),
                                "tags", CkanFacet.builder()
                                        .title("Keywords")
                                        .items(List.of(
                                                CkanValueLabel.builder()
                                                        .name("synthetic")
                                                        .displayName("synthetic")
                                                        .build()))
                                        .build()))
                        .build())
                .build();

        var builder = new CkanFilterBuilder(new StubCkanQueryApi(response), datasetsConfig);
        var filters = builder.build(null, "en");

        var modified = findFilter(filters, "modified");
        assertThat(modified.getType()).isEqualTo(FilterType.DATETIME);
        assertThat(modified.getRange()).isNotNull();
        assertThat(modified.getRange().getMin()).isEqualTo("2024-01-01T00:00Z");
        assertThat(modified.getRange().getMax()).isEqualTo("2024-12-31T23:59:59Z");

        var number = findFilter(filters, "number_of_records");
        assertThat(number.getType()).isEqualTo(FilterType.NUMBER);
        assertThat(number.getRange()).isNotNull();
        assertThat(number.getRange().getMin()).isEqualTo("10");
        assertThat(number.getRange().getMax()).isEqualTo("250");

        var tags = findFilter(filters, "tags");
        assertThat(tags.getValues())
                .extracting(ValueLabel::getValue)
                .containsExactly("synthetic");
    }

    @Test
    void leavesRangeEmptyWhenFacetItemsAreNotParsable() {
        var response = PackagesSearchResponse.builder()
                .result(PackagesSearchResult.builder()
                        .searchFacets(Map.of(
                                "number_of_records", CkanFacet.builder()
                                        .items(List.of(
                                                CkanValueLabel.builder()
                                                        .name("not-a-number")
                                                        .displayName("N/A")
                                                        .build()))
                                        .build()))
                        .build())
                .build();

        var builder = new CkanFilterBuilder(new StubCkanQueryApi(response), datasetsConfig);
        var filters = builder.build(null, "en");

        var number = findFilter(filters, "number_of_records");
        assertThat(number.getRange()).isNull();
    }

    private Filter findFilter(List<Filter> filters, String key) {
        return filters.stream()
                .filter(filter -> key.equals(filter.getKey()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Filter %s not present".formatted(key)));
    }

    @Vetoed
    private static final class StubCkanQueryApi implements CkanQueryApi {

        private final PackagesSearchResponse response;

        private StubCkanQueryApi(PackagesSearchResponse response) {
            this.response = response;
        }

        @Override
        public PackagesSearchResponse packageSearch(String acceptLanguage,
                PackageSearchRequest packageSearchRequest) {
            return response;
        }

        @Override
        public io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackageShowResponse packageShow(
                String id, String acceptLanguage) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String retrieveDatasetInFormat(String id, String format, String authorization) {
            throw new UnsupportedOperationException();
        }
    }

    @Vetoed
    private static final class TestDatasetsConfig implements DatasetsConfig {

        private static final Set<Filter> FILTERS = Set.of(
                new TestFilter("modified", true, false),
                new TestFilter("number_of_records", false, true),
                new TestFilter("tags", false, false));

        @Override
        public String filters() {
            return "modified,number_of_records,tags";
        }

        @Override
        public String noGroupKey() {
            return "DEFAULT";
        }

        @Override
        public List<FilterGroup> filterGroups() {
            return List.of(new TestFilterGroup("DEFAULT", FILTERS));
        }
    }

    @Vetoed
    private record TestFilter(String key, Boolean isDateTime, Boolean isNumber) implements
            DatasetsConfig.Filter {

        @Override
        public String key() {
            return key;
        }

        @Override
        public Boolean isDateTime() {
            return isDateTime;
        }

        @Override
        public Boolean isNumber() {
            return isNumber;
        }
    }

    @Vetoed
    private record TestFilterGroup(String key, Set<DatasetsConfig.Filter> filters)
            implements DatasetsConfig.FilterGroup {

        @Override
        public String key() {
            return key;
        }

        @Override
        public Set<DatasetsConfig.Filter> filters() {
            return filters;
        }
    }
}
