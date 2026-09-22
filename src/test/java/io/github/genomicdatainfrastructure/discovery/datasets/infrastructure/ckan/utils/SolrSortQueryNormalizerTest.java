// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolrSortQueryNormalizerTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void normalize_whenNullOrBlank_leavesInputUnchanged(String value) {
        assertEquals(value, SolrSortQueryNormalizer.normalize(value));
    }

    @Test
    void normalize_withScoreBeforeTitleString_movesScoreAfterTitleString() {
        // CAT-409: score as primary key defeats the intended alphabetical sort.
        var input = "score desc, title_string asc";
        var expected = "title_string asc, score desc";
        assertEquals(expected, SolrSortQueryNormalizer.normalize(input));
    }

    @Test
    void normalize_withScoreBeforeMetadataModified_leavesOrderUnchanged() {
        // Already the API's default ordering, so this is a no-op.
        var input = "score desc, metadata_modified desc";
        assertEquals(input, SolrSortQueryNormalizer.normalize(input));
    }

    @Test
    void normalize_withMetadataModifiedOnlyAndScoreMissing_prependsScoreDesc() {
        // Reproduces CAT-409: a client sort string drops score entirely.
        var input = "metadata_modified desc";
        var expected = "score desc, metadata_modified desc";
        assertEquals(expected, SolrSortQueryNormalizer.normalize(input));
    }

    @Test
    void normalize_withTitleStringOnlyAndScoreMissing_appendsScoreDescAfter() {
        var input = "title_string asc";
        var expected = "title_string asc, score desc";
        assertEquals(expected, SolrSortQueryNormalizer.normalize(input));
    }

    @Test
    void normalize_withNeitherTitleStringNorMetadataModified_prependsScoreDesc() {
        // No stated rule; defaults to the API's relevance-first philosophy.
        var input = "organization asc";
        var expected = "score desc, organization asc";
        assertEquals(expected, SolrSortQueryNormalizer.normalize(input));
    }

    @Test
    void normalize_withBothTitleStringAndMetadataModified_placesScoreAfterTitleString() {
        // No stated rule; title_string takes precedence over relevance.
        var input = "title_string desc, metadata_modified asc";
        var expected = "title_string desc, score desc, metadata_modified asc";
        assertEquals(expected, SolrSortQueryNormalizer.normalize(input));
    }

    @Test
    void normalize_withExplicitNonDefaultScoreDirection_preservesDirection() {
        // A client-specified score direction is repositioned, not overwritten.
        var input = "score asc, title_string desc";
        var expected = "title_string desc, score asc";
        assertEquals(expected, SolrSortQueryNormalizer.normalize(input));
    }

    @Test
    void normalize_whenAppliedTwice_isIdempotent() {
        var input = "score desc, title_string asc";
        var normalizedOnce = SolrSortQueryNormalizer.normalize(input);
        var normalizedTwice = SolrSortQueryNormalizer.normalize(normalizedOnce);
        assertEquals(normalizedOnce, normalizedTwice);
    }
}
