// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Repositions the "score" clause in a client-supplied Solr {@code sort}
 * string.
 * Rule: "score" goes after "title_string" (alphabetical wins) or before
 * "metadata_modified" (relevance wins); if neither is present, it goes first.
 */
@UtilityClass
public class SolrSortQueryNormalizer {

    private final String SCORE_FIELD = "score";
    private final String TITLE_FIELD = "title_string";
    private final String MODIFIED_FIELD = "metadata_modified";
    private final String DEFAULT_SCORE_CLAUSE = "score desc";
    private final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    public String normalize(String sort) {
        if (sort == null || sort.isBlank()) {
            return sort;
        }

        var clauses = new ArrayList<>(Arrays.stream(sort.split(","))
                .map(String::trim)
                .filter(clause -> !clause.isEmpty())
                .toList());

        var scoreIndex = indexOfField(clauses, SCORE_FIELD);
        var scoreClause = scoreIndex >= 0 ? clauses.remove(scoreIndex) : DEFAULT_SCORE_CLAUSE;

        var titleIndex = indexOfField(clauses, TITLE_FIELD);
        if (titleIndex >= 0) {
            clauses.add(titleIndex + 1, scoreClause);
            return String.join(", ", clauses);
        }

        var modifiedIndex = indexOfField(clauses, MODIFIED_FIELD);
        if (modifiedIndex >= 0) {
            clauses.add(modifiedIndex, scoreClause);
            return String.join(", ", clauses);
        }

        clauses.add(0, scoreClause);
        return String.join(", ", clauses);
    }

    private int indexOfField(List<String> clauses, String field) {
        for (var i = 0; i < clauses.size(); i++) {
            if (WHITESPACE_PATTERN.split(clauses.get(i), 2)[0].equals(field)) {
                return i;
            }
        }
        return -1;
    }
}
