// SPDX-FileCopyrightText: 2026 Health-RI
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.discovery.model.RetrievedDataset;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFilterHelpTextsResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackage;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanValueLabel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class CkanDatasetHelpTextServiceTest {

    @Test
    void enrichMapsCkanFieldNamesToDatasetPropertyNames() {
        var ckanQueryApi = mock(CkanQueryApi.class);
        var service = new CkanDatasetHelpTextService(ckanQueryApi, new ObjectMapper());
        var dataset = RetrievedDataset.builder().id("dataset-1").build();
        var ckanPackage = CkanPackage.builder()
                .type(CkanValueLabel.builder().name("dataset_series").build())
                .build();

        when(ckanQueryApi.gdiDatasetHelpTextsShow(anyString(), anyString(), anyString()))
                .thenReturn(CkanFilterHelpTextsResponse.builder()
                        .result(Map.of(
                                "title_translated", "A descriptive title.",
                                "access_rights", "Access conditions.",
                                "in_series", "Dataset series this dataset belongs to.",
                                "resource_fields.name_translated", "A resource title.",
                                "resource_fields.format", "Resource format.",
                                "resource_fields.access_services.title",
                                "A data service title.",
                                "unknown_field", "Not exposed."
                        ))
                        .build());

        service.enrich(dataset, ckanPackage, "nl");

        assertThat(dataset.getHelpText())
                .containsEntry("title", "A descriptive title.")
                .containsEntry("accessRights", "Access conditions.")
                .containsEntry("inSeries", "Dataset series this dataset belongs to.")
                .containsEntry("distributions.title", "A resource title.")
                .containsEntry("samples.title", "A resource title.")
                .containsEntry("analytics.title", "A resource title.")
                .containsEntry("distributions.format", "Resource format.")
                .containsEntry("samples.format", "Resource format.")
                .containsEntry("analytics.format", "Resource format.")
                .containsEntry("distributions.accessService.title", "A data service title.")
                .doesNotContainKey("unknown_field");
    }

    @Test
    void enrichNormalizesMultilineHelpTextValues() {
        var ckanQueryApi = mock(CkanQueryApi.class);
        var service = new CkanDatasetHelpTextService(ckanQueryApi, new ObjectMapper());
        var dataset = RetrievedDataset.builder().id("dataset-1").build();

        when(ckanQueryApi.gdiDatasetHelpTextsShow(anyString(), anyString(), anyString()))
                .thenReturn(CkanFilterHelpTextsResponse.builder()
                        .result(Map.of(
                                "health_theme",
                                "A category of the Dataset or tag describing the Dataset.\n",
                                "access_rights",
                                "Information that indicates whether\nthis dataset is open or restricted."
                        ))
                        .build());

        service.enrich(dataset, CkanPackage.builder().build(), "en");

        assertThat(dataset.getHelpText())
                .containsEntry(
                        "healthTheme",
                        "A category of the Dataset or tag describing the Dataset."
                )
                .containsEntry(
                        "accessRights",
                        "Information that indicates whether this dataset is open or restricted."
                );
    }

    @Test
    void enrichForwardsPreferredLanguageDatasetTypeAndRequestedKeys() {
        var ckanQueryApi = mock(CkanQueryApi.class);
        var service = new CkanDatasetHelpTextService(ckanQueryApi, new ObjectMapper());
        var capturedLanguage = new String[1];
        var capturedType = new String[1];
        var capturedKeys = new String[1];

        when(ckanQueryApi.gdiDatasetHelpTextsShow(anyString(), anyString(), anyString()))
                .thenAnswer(invocation -> {
                    capturedLanguage[0] = invocation.getArgument(0, String.class);
                    capturedType[0] = invocation.getArgument(1, String.class);
                    capturedKeys[0] = invocation.getArgument(2, String.class);
                    return CkanFilterHelpTextsResponse.builder()
                            .result(Map.of())
                            .build();
                });

        service.enrich(
                RetrievedDataset.builder().id("dataset-1").build(),
                CkanPackage.builder()
                        .type(CkanValueLabel.builder().name("dataset_series").build())
                        .build(),
                "nl"
        );

        assertThat(capturedLanguage[0]).isEqualTo("nl");
        assertThat(capturedType[0]).isEqualTo("dataset_series");
        assertThat(capturedKeys[0])
                .contains("\"title_translated\"")
                .contains("\"access_rights\"")
                .contains("\"resource_fields.name_translated\"")
                .contains("\"resource_fields.access_services.title\"");
    }

    @Test
    void enrichAddsJoinedDatasetSeriesHelpTextWithInSeriesPrefix() {
        var ckanQueryApi = mock(CkanQueryApi.class);
        var service = new CkanDatasetHelpTextService(ckanQueryApi, new ObjectMapper());
        var dataset = RetrievedDataset.builder().id("dataset-1").build();

        when(ckanQueryApi.gdiDatasetHelpTextsShow(anyString(), anyString(), anyString()))
                .thenAnswer(invocation -> {
                    var type = invocation.getArgument(1, String.class);
                    if ("dataset_series".equals(type)) {
                        return CkanFilterHelpTextsResponse.builder()
                                .result(Map.of(
                                        "title_translated",
                                        "A descriptive title for the dataset series.",
                                        "notes_translated", "A description of the dataset series.",
                                        "frequency", "The series publication frequency.",
                                        "temporal_coverage",
                                        "The temporal period the series covers."
                                ))
                                .build();
                    }

                    return CkanFilterHelpTextsResponse.builder()
                            .result(Map.of("title_translated", "A descriptive title."))
                            .build();
                });

        service.enrich(
                dataset,
                CkanPackage.builder().inSeries(List.of("series-1")).build(),
                "en"
        );

        assertThat(dataset.getHelpText())
                .containsEntry("title", "A descriptive title.")
                .containsEntry("inSeries.title", "A descriptive title for the dataset series.")
                .containsEntry("inSeries.description", "A description of the dataset series.")
                .containsEntry("inSeries.frequency", "The series publication frequency.")
                .containsEntry("inSeries.temporalCoverage",
                        "The temporal period the series covers.");
    }

    @Test
    void enrichKeepsDatasetHelpTextWhenJoinedSeriesHelpTextRequestFails() {
        var ckanQueryApi = mock(CkanQueryApi.class);
        var service = new CkanDatasetHelpTextService(ckanQueryApi, new ObjectMapper());
        var dataset = RetrievedDataset.builder().id("dataset-1").build();

        when(ckanQueryApi.gdiDatasetHelpTextsShow(anyString(), anyString(), anyString()))
                .thenAnswer(invocation -> {
                    var type = invocation.getArgument(1, String.class);
                    if ("dataset_series".equals(type)) {
                        throw new RuntimeException("CKAN unavailable");
                    }

                    return CkanFilterHelpTextsResponse.builder()
                            .result(Map.of("title_translated", "A descriptive title."))
                            .build();
                });

        service.enrich(
                dataset,
                CkanPackage.builder().inSeries(List.of("series-1")).build(),
                "en"
        );

        assertThat(dataset.getHelpText())
                .containsEntry("title", "A descriptive title.")
                .doesNotContainKey("inSeries.title");
    }

    @Test
    void enrichFallsBackToDatasetTypeWhenCkanPackageHasNoType() {
        var ckanQueryApi = mock(CkanQueryApi.class);
        var service = new CkanDatasetHelpTextService(ckanQueryApi, new ObjectMapper());
        var capturedType = new String[1];

        when(ckanQueryApi.gdiDatasetHelpTextsShow(anyString(), anyString(), anyString()))
                .thenAnswer(invocation -> {
                    capturedType[0] = invocation.getArgument(1, String.class);
                    return CkanFilterHelpTextsResponse.builder()
                            .result(Map.of())
                            .build();
                });

        service.enrich(
                RetrievedDataset.builder().id("dataset-1").build(),
                CkanPackage.builder().build(),
                "en"
        );

        assertThat(capturedType[0]).isEqualTo("dataset");
    }

    @Test
    void enrichLeavesDatasetUnchangedWhenCkanHelpTextRequestFails() {
        var ckanQueryApi = mock(CkanQueryApi.class);
        var service = new CkanDatasetHelpTextService(ckanQueryApi, new ObjectMapper());
        var dataset = RetrievedDataset.builder().id("dataset-1").build();

        when(ckanQueryApi.gdiDatasetHelpTextsShow(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("CKAN unavailable"));

        var result = service.enrich(dataset, CkanPackage.builder().build(), "en");

        assertThat(result).isSameAs(dataset);
        assertThat(dataset.getHelpText()).isNull();
    }
}
