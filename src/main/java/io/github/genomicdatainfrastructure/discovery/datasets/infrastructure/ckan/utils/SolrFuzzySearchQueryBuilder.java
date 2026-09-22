// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

/**
 * Appends Solr's fuzzy operator ({@code ~}) to free-text terms so typos still
 * match, while keeping dissimilar words (e.g. "RNA" vs "DNA", ~66% similar)
 * apart. Lucene only supports integer edit distances (max 2), so a 75%
 * similarity target is converted per word: {@code floor(length * 0.25)},
 * capped at 2. Runs after {@link SolrQueryTextSanitizer#escape}.
 */
@UtilityClass
public class SolrFuzzySearchQueryBuilder {

    private final int SIMILARITY_THRESHOLD_PERCENT = 75;
    private final int MAX_LUCENE_EDITS = 2;
    private final Pattern TERM = Pattern.compile("\\S+");

    public String applyFuzzy(String sanitizedQuery) {
        if (sanitizedQuery == null || sanitizedQuery.isBlank()) {
            return sanitizedQuery;
        }

        var matcher = TERM.matcher(sanitizedQuery);
        var result = new StringBuilder();
        var lastEnd = 0;
        while (matcher.find()) {
            result.append(sanitizedQuery, lastEnd, matcher.start());
            var term = matcher.group();
            result.append(term).append(fuzzySuffix(term));
            lastEnd = matcher.end();
        }
        result.append(sanitizedQuery, lastEnd, sanitizedQuery.length());
        return result.toString();
    }

    private String fuzzySuffix(String term) {
        if (isNumeric(term)) {
            return "";
        }

        var maxEdits = Math.min(
                term.length() * (100 - SIMILARITY_THRESHOLD_PERCENT) / 100,
                MAX_LUCENE_EDITS);
        return maxEdits > 0 ? "~" + maxEdits : "";
    }

    private boolean isNumeric(String term) {
        return term.chars().allMatch(Character::isDigit);
    }
}
