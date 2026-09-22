// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolrFuzzySearchQueryBuilderTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void given_null_or_blank_query_should_leave_it_unchanged(String value) {
        assertEquals(value, SolrFuzzySearchQueryBuilder.applyFuzzy(value));
    }

    @Test
    void given_short_word_should_add_no_suffix() {
        var input = "DNA";
        assertEquals(input, SolrFuzzySearchQueryBuilder.applyFuzzy(input));
    }

    @Test
    void given_word_long_enough_for_one_edit_should_add_fuzzy_suffix() {
        var input = "codon";
        var expected = "codon~1";
        assertEquals(expected, SolrFuzzySearchQueryBuilder.applyFuzzy(input));
    }

    @Test
    void given_very_long_word_should_cap_at_lucene_max_edits_of_two() {
        var input = "abcdefghijklmnopqrstuvwxyzabcd";
        var expected = input + "~2";
        assertEquals(expected, SolrFuzzySearchQueryBuilder.applyFuzzy(input));
    }

    @Test
    void given_multiple_words_should_apply_suffix_per_word_independently() {
        var input = "chromosome DNA";
        var expected = "chromosome~2 DNA";
        assertEquals(expected, SolrFuzzySearchQueryBuilder.applyFuzzy(input));
    }

    @Test
    void given_query_with_runs_of_whitespace_should_preserve_spacing() {
        var input = "chromosome     DNA";
        var expected = "chromosome~2     DNA";
        assertEquals(expected, SolrFuzzySearchQueryBuilder.applyFuzzy(input));
    }

    @Test
    void given_purely_numeric_token_should_leave_it_unchanged() {
        var input = "12345678901";
        assertEquals(input, SolrFuzzySearchQueryBuilder.applyFuzzy(input));
    }

    @Test
    void given_already_escaped_reserved_character_should_treat_it_as_part_of_the_term() {
        var input = "stool\\-based";
        var expected = "stool\\-based~2";
        assertEquals(expected, SolrFuzzySearchQueryBuilder.applyFuzzy(input));
    }
}
