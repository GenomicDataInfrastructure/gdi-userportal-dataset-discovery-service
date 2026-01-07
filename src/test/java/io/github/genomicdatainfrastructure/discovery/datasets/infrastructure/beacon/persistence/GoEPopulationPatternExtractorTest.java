// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.config.GoEPopulationPatternConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoEPopulationPatternExtractorTest {

    @Mock
    private GoEPopulationPatternConfig config;

    @InjectMocks
    private GoEPopulationPatternExtractor extractor;

    @Test
    void testExtractPopulationNull() {
        assertNull(extractor.extractPopulation(null));
    }

    @Test
    void testExtractPopulationEmpty() {
        assertNull(extractor.extractPopulation(""));
    }

    @Test
    void testExtractPopulationValid() {
        when(config.separator()).thenReturn("_");
        when(config.countryPosition()).thenReturn(0);
        when(config.sexPosition()).thenReturn(1);

        assertEquals("PT_M", extractor.extractPopulation("PT_M"));
    }

    @Test
    void testExtractPopulationNegativePosition() {
        when(config.separator()).thenReturn("_");
        when(config.countryPosition()).thenReturn(-1);
        when(config.sexPosition()).thenReturn(1);

        assertEquals("M", extractor.extractPopulation("PT_M"));
    }

    @Test
    void testExtractPopulationOutOfBounds() {
        when(config.separator()).thenReturn("_");
        when(config.countryPosition()).thenReturn(5);
        when(config.sexPosition()).thenReturn(5);

        assertNull(extractor.extractPopulation("PT"));
    }

    @Test
    void testExtractPopulationInvalidSex() {
        when(config.separator()).thenReturn("_");
        when(config.countryPosition()).thenReturn(0);
        when(config.sexPosition()).thenReturn(1);

        assertEquals("PT", extractor.extractPopulation("PT_X"));
    }
}
