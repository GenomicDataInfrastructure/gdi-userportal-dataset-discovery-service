// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils;

import lombok.experimental.UtilityClass;

/**
 * Escapes Lucene/Solr reserved characters in free-text search input.
 * <p>
 * The dataset search endpoint accepts a single free-text {@code query} string
 * (e.g. a title or abstract) that is forwarded to CKAN/Solr's
 * {@code q} parameter. Solr's query parser treats certain characters as query
 * syntax rather than literal text - most notably {@code :}, which delimits a
 * field name from its value (e.g. {@code fieldname:value}).
 */
@UtilityClass
public class SolrQueryTextSanitizer {

    /**
     * Characters with special meaning to Solr's query parser that must be
     * backslash-escaped so free text is matched literally. See:
     * https://solr.apache.org/guide/solr/latest/query-guide/standard-query-parser.html#escaping-special-characters
     * Note: Unlike {@code escapeQueryChars}, whitespace is intentionally left
     * un-escaped: escaping a space turns it into a literal character within a
     * single token instead of a word separator, which would fuse "colorectal cancer"
     * into one unmatchable term rather than two words ANDed together -
     * exactly the kind of over-correction that would trade one zero-result bug
     * for another.
     **/

    private final String RESERVED_CHARACTERS = "+-&|!(){}[]^\"~*?:\\/;";

    public String escape(String query) {
        if (query == null || query.isBlank()) {
            return query;
        }

        var escaped = new StringBuilder(query.length() * 2);
        for (var character : query.toCharArray()) {
            if (RESERVED_CHARACTERS.indexOf(character) >= 0) {
                escaped.append('\\');
            }
            escaped.append(character);
        }
        return escaped.toString();
    }
}