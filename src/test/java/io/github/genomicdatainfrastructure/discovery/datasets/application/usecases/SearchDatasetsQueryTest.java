// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.application.usecases;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence.BeaconDatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence.CkanDatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SearchDatasetsQueryTest {

    private DatasetsRepository repository;

    private BeaconDatasetIdsCollector beaconCollector;

    private CkanDatasetIdsCollector ckanCollector;

    private SearchDatasetsQuery underTest;

    @BeforeEach
    void setUp() {
        repository = mock(DatasetsRepository.class);
        beaconCollector = mock(BeaconDatasetIdsCollector.class);
        ckanCollector = mock(CkanDatasetIdsCollector.class);

        underTest = new SearchDatasetsQuery(repository, beaconCollector, ckanCollector);
    }

    @Test
    void testExecute_withIncludeBeaconFalse_shouldUseCkanOnly() {
        var query = DatasetSearchQuery.builder()
                .includeBeacon(false)
                .build();
        var accessToken = "token";

        when(ckanCollector.collect(any(), any())).thenReturn(Map.of("id1", 10, "id2", 20));

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
        verifyNoInteractions(beaconCollector); // Beacon should not be called
    }

    @Test
    void testExecute_withIncludeBeaconTrue_shouldUseBeacon() {
        var query = DatasetSearchQuery.builder()
                .includeBeacon(true)
                .build();
        var accessToken = "token";

        when(ckanCollector.collect(any(), any())).thenReturn(Map.of("id1", 10, "id2", 20));
        when(beaconCollector.collect(any(), any())).thenReturn(Map.of("id1", 15, "id3", 30));

        var dataset1 = mockDataset("id1");
        when(repository.search(any(), any(), any(), any(), any(), any())).thenReturn(List.of(
                dataset1));

        var response = underTest.execute(query, accessToken, "en");

        // Should use intersection (only id1 is in both)
        assertEquals(1, response.getCount());
        assertEquals("id1", response.getResults().get(0).getIdentifier());
        assertEquals(10, response.getResults().get(0).getRecordsCount()); // CKAN record count

        verify(repository).search(eq(Set.of("id1")), any(), any(), any(), eq(accessToken), eq(
                "en"));
        verify(ckanCollector).collect(any(), any());
        verify(beaconCollector).collect(any(), any());
    }

    @Test
    void testExecute_whenBeaconReturnsError_capturesError() {
        var query = DatasetSearchQuery.builder()
                .includeBeacon(true)
                .build();
        var accessToken = "token";

        // CKAN always succeeds
        when(ckanCollector.collect(any(), any())).thenReturn(Map.of("id1", 10));

        // Beacon throws WebApplicationException with 401 status
        var mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(401);
        when(mockResponse.readEntity(String.class)).thenReturn("Unauthorized");
        var exception = new WebApplicationException("Unauthorized", mockResponse);
        when(beaconCollector.collect(any(), any())).thenThrow(exception);

        var dataset1 = mockDataset("id1");
        when(repository.search(any(), any(), any(), any(), any(), any())).thenReturn(List.of(
                dataset1));

        var response = underTest.execute(query, accessToken, "en");

        // Should fall back to CKAN-only results
        assertNotNull(response);
        assertEquals(1, response.getCount());
        assertEquals("id1", response.getResults().get(0).getIdentifier());

        // Should capture the error message
        assertNotNull(response.getBeaconError());
        assertEquals("Beacon service authentication failed. Please check your credentials.",
                response.getBeaconError());

        // Verify both collectors were called
        verify(ckanCollector).collect(any(), any());
        verify(beaconCollector).collect(any(), any());
    }

    @Test
    void testExecute_whenBeaconThrowsGenericException_fallsBackToCkan() {
        when(ckanCollector.collect(any(), any())).thenReturn(Map.of("id1", 10));
        when(beaconCollector.collect(any(), any())).thenThrow(new RuntimeException(
                "Connection timeout"));
        when(repository.search(any(), any(), any(), any(), any(), any())).thenReturn(List.of(
                mockDataset("id1")));

        var response = underTest.execute(DatasetSearchQuery.builder().includeBeacon(true).build(),
                "token", "en");

        assertEquals("Beacon service unavailable: Connection timeout", response.getBeaconError());
    }

    @Test
    void testExecute_whenBeaconReturns403_capturesError() {
        when(ckanCollector.collect(any(), any())).thenReturn(Map.of("id1", 10));
        var mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(403);
        when(beaconCollector.collect(any(), any())).thenThrow(new WebApplicationException(
                "Forbidden",
                mockResponse));
        when(repository.search(any(), any(), any(), any(), any(), any())).thenReturn(List.of(
                mockDataset("id1")));

        var response = underTest.execute(DatasetSearchQuery.builder().includeBeacon(true).build(),
                "token", "en");

        assertTrue(response.getBeaconError().contains("Access to Beacon service denied"));
    }

    @Test
    void testExecute_whenBeaconReturns500_capturesError() {
        when(ckanCollector.collect(any(), any())).thenReturn(Map.of("id1", 10));
        var mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(500);
        when(beaconCollector.collect(any(), any())).thenThrow(new WebApplicationException(
                "Internal Error", mockResponse));
        when(repository.search(any(), any(), any(), any(), any(), any())).thenReturn(List.of(
                mockDataset("id1")));

        var response = underTest.execute(DatasetSearchQuery.builder().includeBeacon(true).build(),
                "token", "en");

        assertTrue(response.getBeaconError().contains("internal error"));
    }

    @Test
    void testExecute_whenBeaconReturnsUnknownStatus_capturesError() {
        when(ckanCollector.collect(any(), any())).thenReturn(Map.of("id1", 10));
        var mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(418);
        when(beaconCollector.collect(any(), any())).thenThrow(new WebApplicationException("Teapot",
                mockResponse));
        when(repository.search(any(), any(), any(), any(), any(), any())).thenReturn(List.of(
                mockDataset("id1")));

        var response = underTest.execute(DatasetSearchQuery.builder().includeBeacon(true).build(),
                "token", "en");

        assertTrue(response.getBeaconError().contains("HTTP 418"));
    }

    private SearchedDataset mockDataset(String id) {
        return SearchedDataset.builder()
                .identifier(id)
                .recordsCount(null)
                .build();
    }
}
