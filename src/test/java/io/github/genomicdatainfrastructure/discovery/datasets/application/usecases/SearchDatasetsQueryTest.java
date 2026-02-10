// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.application.usecases;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.BeaconDatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;
import jakarta.enterprise.inject.Instance;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SearchDatasetsQueryTest {

    private DatasetsRepository repository;

    private Instance<DatasetIdsCollector> collectors;

    private DatasetIdsCollector collector1;

    private DatasetIdsCollector collector2;

    private SearchDatasetsQuery underTest;

    @BeforeEach
    void setUp() {
        repository = mock(DatasetsRepository.class);
        collector1 = mock(DatasetIdsCollector.class);
        collector2 = mock(DatasetIdsCollector.class);
        collectors = mock(Instance.class);

        underTest = new SearchDatasetsQuery(repository, collectors);
    }

    @Test
    void testExecute_withNoCollectors() {
        var query = new DatasetSearchQuery();
        var accessToken = "token";

        when(collectors.stream()).thenReturn(Stream.of());

        var response = underTest.execute(query, accessToken, null);

        assertEquals(0, response.getCount());
        assertEquals(0, response.getResults().size());

        verify(repository).search(eq(Set.of()), any(), any(), any(), eq(accessToken), isNull());
    }

    @Test
    void testExecute_withIntersection() {
        var query = new DatasetSearchQuery();
        var accessToken = "token";

        when(collectors.stream()).thenReturn(Stream.of(collector1, collector2));

        when(collector1.collect(any(), any())).thenReturn(Map.of("id1", 10, "id2", 20));
        when(collector2.collect(any(), any())).thenReturn(Map.of("id1", 15, "id3", 30));

        var dataset1 = mockDataset("id1");
        var dataset2 = mockDataset("id2");
        when(repository.search(any(), any(), any(), any(), any(), any())).thenReturn(List.of(
                dataset1,
                dataset2));

        var response = underTest.execute(query, accessToken, "en");

        assertEquals(1, response.getCount());
        assertEquals("id1", response.getResults().getFirst().getIdentifier());
        assertEquals(10, response.getResults().getFirst().getRecordsCount());

        verify(repository).search(eq(Set.of("id1")), any(), any(), any(), eq(accessToken), eq(
                "en"));
    }

    @Test
    void testExecute_withNullRecordsCount() {
        var query = new DatasetSearchQuery();
        var accessToken = "token";

        when(collectors.stream()).thenReturn(Stream.of(collector1));
        var returnValue = new HashMap<String, Integer>();
        returnValue.put("id1", null);
        when(collector1.collect(any(), any())).thenReturn(returnValue);

        var dataset1 = mockDataset("id1");
        when(repository.search(any(), any(), any(), any(), any(), any())).thenReturn(List.of(
                dataset1));

        DatasetsSearchResponse response = underTest.execute(query, accessToken, null);

        assertEquals(1, response.getCount());
        assertEquals("id1", response.getResults().getFirst().getIdentifier());
        assertEquals(null, response.getResults().getFirst().getRecordsCount()); // null record count

        verify(repository).search(eq(Set.of("id1")), any(), any(), any(), eq(accessToken),
                isNull());
    }

    @Test
    void testExecute_withIncludeBeaconFalse_shouldUseCkanOnly() {
        var query = DatasetSearchQuery.builder()
                .includeBeacon(false)
                .build();
        var accessToken = "token";

        var ckanCollector = mock(
                io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence.CkanDatasetIdsCollector.class);
        when(ckanCollector.collect(any(), any())).thenReturn(Map.of("id1", 10, "id2", 20));
        when(collectors.stream()).thenReturn(Stream.of(ckanCollector));

        var dataset1 = mockDataset("id1");
        var dataset2 = mockDataset("id2");
        when(repository.search(any(), any(), any(), any(), any(), any())).thenReturn(List.of(
                dataset1, dataset2));

        var response = underTest.execute(query, accessToken, "en");

        assertEquals(2, response.getCount());
        assertEquals(2, response.getResults().size());
        assertEquals(null, response.getResults().get(0).getRecordsCount());

        verify(repository).search(eq(Set.of("id1", "id2")), any(), any(), any(), eq(accessToken),
                eq("en"));
        verify(ckanCollector).collect(any(), any());
    }

    @Test
    void testExecute_withIncludeBeaconTrue_shouldUseBeacon() {
        var query = DatasetSearchQuery.builder()
                .includeBeacon(true)
                .build();
        var accessToken = "token";

        when(collectors.stream()).thenReturn(Stream.of(collector1, collector2));
        when(collector1.collect(any(), any())).thenReturn(Map.of("id1", 10, "id2", 20));
        when(collector2.collect(any(), any())).thenReturn(Map.of("id1", 15, "id3", 30));

        var dataset1 = mockDataset("id1");
        when(repository.search(any(), any(), any(), any(), any(), any())).thenReturn(List.of(
                dataset1));

        var response = underTest.execute(query, accessToken, "en");

        assertEquals(1, response.getCount());
        assertEquals("id1", response.getResults().getFirst().getIdentifier());
        assertEquals(10, response.getResults().getFirst().getRecordsCount());

        verify(repository).search(eq(Set.of("id1")), any(), any(), any(), eq(accessToken), eq(
                "en"));
        verify(collector1).collect(any(), any());
        verify(collector2).collect(any(), any());
    }

    @Test
    void testExecute_withIncludeBeaconNull_shouldDefaultToBeacon() {
        var query = DatasetSearchQuery.builder()
                .includeBeacon(null)
                .build();
        var accessToken = "token";

        when(collectors.stream()).thenReturn(Stream.of(collector1));
        when(collector1.collect(any(), any())).thenReturn(Map.of("id1", 10));

        var dataset1 = mockDataset("id1");
        when(repository.search(any(), any(), any(), any(), any(), any())).thenReturn(List.of(
                dataset1));

        var response = underTest.execute(query, accessToken, null);

        assertEquals(1, response.getCount());
        assertEquals(10, response.getResults().getFirst().getRecordsCount());

        verify(collector1).collect(any(), any());
    }

    @Test
    void testExecute_ckanOnlyWithNoCkanCollector_shouldThrowException() {
        var query = DatasetSearchQuery.builder()
                .includeBeacon(false)
                .build();
        var accessToken = "token";

        when(collectors.stream()).thenReturn(Stream.of(collector1, collector2));

        var exception = assertThrows(IllegalStateException.class, () -> {
            underTest.execute(query, accessToken, "en");
        });

        assertEquals("CKAN collector not found", exception.getMessage());
    }

    @Test
    void testExecute_withBeaconCollectorReturningNull_shouldHandleGracefully() {
        var query = DatasetSearchQuery.builder()
                .includeBeacon(true)
                .build();
        var accessToken = "token";

        when(collectors.stream()).thenReturn(Stream.of(collector1, collector2));
        when(collector1.collect(any(), any())).thenReturn(Map.of("id1", 10));
        when(collector2.collect(any(), any())).thenReturn(null);

        var dataset1 = mockDataset("id1");
        when(repository.search(any(), any(), any(), any(), any(), any())).thenReturn(List.of(
                dataset1));

        var response = underTest.execute(query, accessToken, "en");

        assertEquals(1, response.getCount());
        assertEquals("id1", response.getResults().getFirst().getIdentifier());
        assertEquals(10, response.getResults().getFirst().getRecordsCount());

        verify(collector1).collect(any(), any());
        verify(collector2).collect(any(), any());
    }

    @Test
    void testExecute_whenBeaconReturnsError_capturesError() {
        var query = new DatasetSearchQuery();
        var accessToken = "token";

        var beaconCollector = mock(BeaconDatasetIdsCollector.class);

        // Simulate what happens after catch block: collect returns null
        when(beaconCollector.collect(any(), any())).thenReturn(null);
        // Error is stored in lastError
        when(beaconCollector.getLastError()).thenReturn(
                "Beacon service error (HTTP 401): Unauthorized");

        when(collectors.stream()).thenReturn(Stream.of(beaconCollector));
        when(repository.search(any(), any(), any(), any(), any(), any())).thenReturn(List.of());

        var response = underTest.execute(query, accessToken, "en");

        assertNotNull(response.getBeaconError());
        assertEquals("Beacon service error (HTTP 401): Unauthorized", response.getBeaconError());
    }

    private SearchedDataset mockDataset(String id) {
        return SearchedDataset.builder()
                .identifier(id)
                .recordsCount(null)
                .build();
    }
}
