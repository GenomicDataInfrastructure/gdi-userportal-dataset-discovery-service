// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.domain.exceptions.DatasetNotFoundException;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.client.CkanDatasetSeriesExportApi;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.mapper.CkanDatasetsMapper;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CkanDatasetsRepositoryTest {

    @Mock
    CkanQueryApi ckanQueryApi;

    @Mock
    CkanDatasetSeriesExportApi ckanDatasetSeriesExportApi;

    @Mock
    CkanDatasetsMapper ckanDatasetsMapper;

    @InjectMocks
    CkanDatasetsRepository repository;

    @AfterEach
    @SuppressWarnings("unused")
    void noMapperInteractions() {
        verifyNoInteractions(ckanDatasetsMapper);
    }

    @Test
    void given308WithDatasetSeriesRedirect_whenRetrieveDatasetInFormat_thenRetriesDatasetSeries() {
        var id = "series-id";
        var format = "ttl";
        var token = "Bearer test-token";

        when(ckanQueryApi.retrieveDatasetInFormat(id, format, token))
                .thenThrow(new WebApplicationException(Response.status(308)
                        .header("Location", "/dataset_series/" + id + "." + format)
                        .build()));
        when(ckanDatasetSeriesExportApi.retrieveDatasetSeriesInFormat(id, format, token))
                .thenReturn("series ttl content");

        var actual = repository.retrieveDatasetInFormat(id, format, token);

        assertEquals("series ttl content", actual);
        verify(ckanDatasetSeriesExportApi).retrieveDatasetSeriesInFormat(id, format, token);
    }

    @Test
    void given308WithAbsoluteDatasetSeriesRedirect_whenRetrieveDatasetInFormat_thenRetriesDatasetSeries() {
        var id = "series-id";
        var format = "ttl";
        var token = "Bearer test-token";

        when(ckanQueryApi.retrieveDatasetInFormat(id, format, token))
                .thenThrow(new WebApplicationException(Response.status(308)
                        .header("Location", "https://ckan.example/dataset_series/" + id + "."
                                + format)
                        .build()));
        when(ckanDatasetSeriesExportApi.retrieveDatasetSeriesInFormat(id, format, token))
                .thenReturn("series ttl content");

        var actual = repository.retrieveDatasetInFormat(id, format, token);

        assertEquals("series ttl content", actual);
        verify(ckanDatasetSeriesExportApi).retrieveDatasetSeriesInFormat(id, format, token);
    }

    @Test
    void given308WithoutDatasetSeriesRedirect_whenRetrieveDatasetInFormat_thenRethrows() {
        var id = "id";
        var format = "ttl";
        String token = null;

        var exception = new WebApplicationException(Response.status(308)
                .header("Location", "/dataset/" + id + "." + format)
                .build());
        when(ckanQueryApi.retrieveDatasetInFormat(id, format, token)).thenThrow(exception);

        var actual = assertThrows(WebApplicationException.class,
                () -> repository.retrieveDatasetInFormat(id, format, token));

        assertEquals(308, actual.getResponse().getStatus());
        verifyNoInteractions(ckanDatasetSeriesExportApi);
    }

    @Test
    void given308WithMissingLocationHeader_whenRetrieveDatasetInFormat_thenRethrows() {
        var id = "id";
        var format = "ttl";
        String token = null;

        var exception = new WebApplicationException(Response.status(308).build());
        when(ckanQueryApi.retrieveDatasetInFormat(id, format, token)).thenThrow(exception);

        var actual = assertThrows(WebApplicationException.class,
                () -> repository.retrieveDatasetInFormat(id, format, token));

        assertEquals(308, actual.getResponse().getStatus());
        verifyNoInteractions(ckanDatasetSeriesExportApi);
    }

    @Test
    void given308WithInvalidLocationHeader_whenRetrieveDatasetInFormat_thenRethrows() {
        var id = "id";
        var format = "ttl";
        String token = null;

        var exception = new WebApplicationException(Response.status(308)
                .header("Location", "http://[::1")
                .build());
        when(ckanQueryApi.retrieveDatasetInFormat(id, format, token)).thenThrow(exception);

        var actual = assertThrows(WebApplicationException.class,
                () -> repository.retrieveDatasetInFormat(id, format, token));

        assertEquals(308, actual.getResponse().getStatus());
        verifyNoInteractions(ckanDatasetSeriesExportApi);
    }

    @Test
    void given404_whenRetrieveDatasetInFormat_thenThrowsDatasetNotFound() {
        var id = "missing";
        var format = "ttl";
        String token = null;

        when(ckanQueryApi.retrieveDatasetInFormat(id, format, token))
                .thenThrow(new WebApplicationException(Response.status(404).build()));

        var ex = assertThrows(DatasetNotFoundException.class,
                () -> repository.retrieveDatasetInFormat(id, format, token));
        assertEquals("Dataset %s not found".formatted(id), ex.getMessage());
        verifyNoInteractions(ckanDatasetSeriesExportApi);
    }

    @Test
    void given308SeriesRedirectButSeriesReturns404_whenRetrieveDatasetInFormat_thenThrowsDatasetNotFound() {
        var id = "series-missing";
        var format = "ttl";
        String token = null;

        when(ckanQueryApi.retrieveDatasetInFormat(id, format, token))
                .thenThrow(new WebApplicationException(Response.status(308)
                        .header("Location", "/dataset_series/" + id + "." + format)
                        .build()));
        when(ckanDatasetSeriesExportApi.retrieveDatasetSeriesInFormat(id, format, token))
                .thenThrow(new WebApplicationException(Response.status(404).build()));

        var ex = assertThrows(DatasetNotFoundException.class,
                () -> repository.retrieveDatasetInFormat(id, format, token));
        assertEquals("Dataset %s not found".formatted(id), ex.getMessage());
    }

    @Test
    void given308SeriesRedirectButSeriesErrors_whenRetrieveDatasetInFormat_thenRethrowsSeriesException() {
        var id = "series-id";
        var format = "ttl";
        String token = null;

        when(ckanQueryApi.retrieveDatasetInFormat(id, format, token))
                .thenThrow(new WebApplicationException(Response.status(308)
                        .header("Location", "/dataset_series/" + id + "." + format)
                        .build()));
        var seriesException = new WebApplicationException(Response.status(500).build());
        when(ckanDatasetSeriesExportApi.retrieveDatasetSeriesInFormat(id, format, token))
                .thenThrow(seriesException);

        var actual = assertThrows(WebApplicationException.class,
                () -> repository.retrieveDatasetInFormat(id, format, token));

        assertEquals(500, actual.getResponse().getStatus());
    }
}
