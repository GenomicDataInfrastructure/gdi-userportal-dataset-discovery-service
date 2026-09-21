// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolrQueryTextSanitizerTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void escape_whenNullOrBlank_leavesInputUnchanged(String value) {
        assertEquals(value, SolrQueryTextSanitizer.escape(value));
    }

    @Test
    void escape_withPlainText_leavesInputUnchanged() {
        var input = "colorectal cancer screening";
        assertEquals(input, SolrQueryTextSanitizer.escape(input));
    }

    @Test
    void escape_withColon_escapesIt() {
        var input = "screening programmes: a Dutch population-based study";
        var expected = "screening programmes\\: a Dutch population\\-based study";
        assertEquals(expected, SolrQueryTextSanitizer.escape(input));
    }

    @Test
    void escape_withReportedBugTitle_reproducesFix() {
        // Reproduction of the reported issue: the raw title, when forwarded
        // unescaped to Solr, caused a zero-result search because the ':' was
        // parsed as a field:value delimiter (field "programmes" does not
        // exist) rather than as literal text. Escaping it fixes the search.
        var input = "The multitarget faecal immunochemical test for improving "
                + "stool-based colorectal cancer screening programmes: a Dutch "
                + "population-based, paired-design, intervention study";

        var expected = "The multitarget faecal immunochemical test for improving "
                + "stool\\-based colorectal cancer screening programmes\\: a Dutch "
                + "population\\-based, paired\\-design, intervention study";

        assertEquals(expected, SolrQueryTextSanitizer.escape(input));
    }

    @Test
    void escape_withEveryReservedCharacter_escapesAll() {
        var input = "+-&|!(){}[]^\"~*?:\\/;";
        var expected = "\\+\\-\\&\\|\\!\\(\\)\\{\\}\\[\\]\\^\\\"\\~\\*\\?\\:\\\\\\/\\;";
        assertEquals(expected, SolrQueryTextSanitizer.escape(input));
    }

    @Test
    void escape_withWhitespace_leavesItUnescaped() {
        // Escaping whitespace would fuse "colorectal cancer" into a single
        // token instead of two words ANDed together - intentionally not
        // matching ClientUtils.escapeQueryChars' behavior here.
        var input = "colorectal cancer";
        assertEquals(input, SolrQueryTextSanitizer.escape(input));
    }

    @Test
    void escape_withRunsOfWhitespace_leavesThemUnchanged() {
        // Consecutive spaces are not reserved characters and are left as-is.
        // This is safe: Solr's tokenizer treats any run of whitespace as an
        // equivalent term delimiter, so "population-based     study" and
        // "population-based study" produce the same clause list once parsed -
        // unlike the colon case, extra whitespace never changes query meaning.
        var input = "a Dutch population-based     study";
        var expected = "a Dutch population\\-based     study";
        assertEquals(expected, SolrQueryTextSanitizer.escape(input));
    }

    @Test
    void escape_withUnreservedPunctuation_leavesItUnchanged() {
        // Comma and period are not Solr-reserved and must stay untouched.
        var input = "COVID-19, a paired-design study.";
        var expected = "COVID\\-19, a paired\\-design study.";
        assertEquals(expected, SolrQueryTextSanitizer.escape(input));
    }

    @Test
    void escape_withSemicolon_escapesIt() {
        // Semicolon is reserved (matches ClientUtils.escapeQueryChars) even
        // though it is easy to mistake for ordinary punctuation.
        var input = "paired-design; a study";
        var expected = "paired\\-design\\; a study";
        assertEquals(expected, SolrQueryTextSanitizer.escape(input));
    }
}