// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.stream.Stream;

class SolrQueryTextSanitizerTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void given_null_or_blank_query_should_leave_it_unchanged(String value) {
        assertEquals(value, SolrQueryTextSanitizer.escape(value));
    }

    @ParameterizedTest
    @MethodSource("query_text")
    void should_escape_query_text(String input, String expected) {
        assertEquals(expected, SolrQueryTextSanitizer.escape(input));
    }

    private static Stream<Arguments> query_text() {
        return Stream.of(
                Arguments.of("colorectal cancer screening", "colorectal cancer screening"),
                Arguments.of("screening programmes: a Dutch population-based study",
                        "screening programmes\\: a Dutch population\\-based study"),
                Arguments.of("The multitarget faecal immunochemical test for improving "
                        + "stool-based colorectal cancer screening programmes: a Dutch "
                        + "population-based, paired-design, intervention study",
                        "The multitarget faecal immunochemical test for improving "
                                + "stool\\-based colorectal cancer screening programmes\\: a Dutch "
                                + "population\\-based, paired\\-design, intervention study"),
                Arguments.of("+-&|!(){}[]^~*?:\\/;",
                        "\\+\\-\\&\\|\\!\\(\\)\\{\\}\\[\\]\\^\\~\\*\\?\\:\\\\\\/\\;"),
                Arguments.of("colorectal cancer", "colorectal cancer"));
    }

    @Test
    void given_query_with_runs_of_whitespace_should_leave_them_unchanged() {
        var input = "a Dutch population-based     study";
        var expected = "a Dutch population\\-based     study";
        assertEquals(expected, SolrQueryTextSanitizer.escape(input));
    }

    @Test
    void given_query_with_unreserved_punctuation_should_leave_it_unchanged() {
        var input = "COVID-19, a paired-design study.";
        var expected = "COVID\\-19, a paired\\-design study.";
        assertEquals(expected, SolrQueryTextSanitizer.escape(input));
    }

    @Test
    void given_query_with_semicolon_should_escape_it() {
        var input = "paired-design; a study";
        var expected = "paired\\-design\\; a study";
        assertEquals(expected, SolrQueryTextSanitizer.escape(input));
    }
}