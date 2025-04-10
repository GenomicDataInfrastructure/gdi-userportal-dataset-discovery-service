// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.application.usecases;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;
import jakarta.enterprise.inject.Instance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        var response = underTest.execute(query, accessToken);

        assertEquals(0, response.getCount());
        assertEquals(0, response.getResults().size());

        verify(repository).search(eq(Set.of()), any(), any(), any(), eq(accessToken));
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
        when(repository.search(any(), any(), any(), any(), any())).thenReturn(List.of(dataset1,
                dataset2));

        var response = underTest.execute(query, accessToken);

        assertEquals(2, response.getCount());
        assertEquals("id1", response.getResults().getFirst().getIdentifier());
        assertEquals(10, response.getResults().getFirst().getRecordsCount());

        verify(repository).search(eq(Set.of("id1")), any(), any(), any(), eq(accessToken));
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
        when(repository.search(any(), any(), any(), any(), any())).thenReturn(List.of(dataset1));

        DatasetsSearchResponse response = underTest.execute(query, accessToken);

        assertEquals(1, response.getCount());
        assertEquals("id1", response.getResults().getFirst().getIdentifier());
        assertEquals(null, response.getResults().getFirst().getRecordsCount()); // null record count

        verify(repository).search(eq(Set.of("id1")), any(), any(), any(), eq(accessToken));
    }

    private SearchedDataset mockDataset(String id) {
        return SearchedDataset.builder()
                .identifier(id)
                .recordsCount(null)
                .build();
    }
}
