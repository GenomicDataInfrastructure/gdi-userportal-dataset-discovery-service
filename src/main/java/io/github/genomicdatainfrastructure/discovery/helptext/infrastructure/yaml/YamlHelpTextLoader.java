// SPDX-FileCopyrightText: 2026 Health-RI
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.helptext.infrastructure.yaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.genomicdatainfrastructure.discovery.model.HelpText;
import io.github.genomicdatainfrastructure.discovery.model.HelpTextLink;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Loads help text (text + optional links) from a YAML file supplied either as a local file path
 * or an http(s) URL, caching the parsed content per location for a configurable TTL. Used as an
 * alternative to CKAN as a help-text source, see {@code HelpTextConfig}.
 */
@Log
@ApplicationScoped
public class YamlHelpTextLoader {

    private static final String DEFAULT_LANGUAGE = "en";
    private static final String DUMMY_LABEL = "More info";
    private static final String DUMMY_VALUE = "#";
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(CONNECT_TIMEOUT)
            .build();
    private final Clock clock;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public YamlHelpTextLoader() {
        this(Clock.systemUTC());
    }

    YamlHelpTextLoader(Clock clock) {
        this.clock = clock;
    }

    public Optional<HelpText> lookup(String location, Duration ttl, String key,
            String preferredLanguage) {
        if (location == null || location.isBlank() || key == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(load(location, ttl).get(key))
                .map(entry -> resolve(entry, normalizeLanguage(preferredLanguage)));
    }

    private Map<String, YamlHelpTextEntry> load(String location, Duration ttl) {
        var cached = cache.get(location);
        var now = clock.instant();
        if (cached != null && now.isBefore(cached.fetchedAt().plus(ttl))) {
            return cached.entries();
        }

        try {
            var entries = fetchAndParse(location);
            cache.put(location, new CacheEntry(entries, now));
            return entries;
        } catch (Exception exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.log(Level.WARNING, "Could not fetch/parse help text source: " + location,
                    exception);
            return cached != null ? cached.entries() : Map.of();
        }
    }

    private Map<String, YamlHelpTextEntry> fetchAndParse(
            String location) throws IOException, InterruptedException {
        var content = isUrl(location) ? fetchUrl(location) : Files.readString(Path.of(location));
        return yamlMapper.readValue(content, new TypeReference<Map<String, YamlHelpTextEntry>>() {
        });
    }

    private boolean isUrl(String location) {
        return location.startsWith("http://") || location.startsWith("https://");
    }

    private String fetchUrl(String location) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(location))
                .timeout(REQUEST_TIMEOUT)
                .GET()
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new IOException(
                    "Unexpected status code %d fetching help text source: %s".formatted(response
                            .statusCode(), location));
        }
        return response.body();
    }

    private HelpText resolve(YamlHelpTextEntry entry, String preferredLanguage) {
        return HelpText.builder()
                .text(localizeText(entry.text(), preferredLanguage))
                .link(resolveLink(entry.link(), preferredLanguage))
                .build();
    }

    private HelpTextLink resolveLink(YamlHelpTextEntry.YamlHelpTextLink link,
            String preferredLanguage) {
        var label = link == null ? List.<String>of() : localizeLabel(link.label(),
                preferredLanguage);
        var value = link == null || link.value() == null ? List.<String>of() : link.value();

        var targetSize = Math.max(label.size(), value.size());
        return HelpTextLink.builder()
                .label(pad(label, targetSize, DUMMY_LABEL))
                .value(pad(value, targetSize, DUMMY_VALUE))
                .build();
    }

    private String localizeText(Map<String, String> text, String preferredLanguage) {
        if (text == null) {
            return null;
        }
        var value = text.get(preferredLanguage);
        return value != null ? value : text.get(DEFAULT_LANGUAGE);
    }

    /**
     * {@code label} is keyed by language code (falling back to {@code en}); each language's value
     * may be a single string or a list of strings.
     */
    private List<String> localizeLabel(Map<String, JsonNode> label, String preferredLanguage) {
        if (label == null) {
            return List.of();
        }
        var value = label.containsKey(preferredLanguage) ? label.get(preferredLanguage)
                : label.get(DEFAULT_LANGUAGE);
        return value == null ? List.of() : toStringList(value);
    }

    private List<String> toStringList(JsonNode node) {
        if (node.isArray()) {
            var values = new ArrayList<String>();
            node.forEach(element -> values.add(element.asText()));
            return values;
        }
        return List.of(node.asText());
    }

    private List<String> pad(List<String> values, int targetSize, String filler) {
        if (values.size() >= targetSize) {
            return values;
        }
        var padded = new ArrayList<>(values);
        while (padded.size() < targetSize) {
            padded.add(filler);
        }
        return padded;
    }

    private String normalizeLanguage(String preferredLanguage) {
        if (preferredLanguage == null || preferredLanguage.isBlank()) {
            return DEFAULT_LANGUAGE;
        }
        var lang = preferredLanguage.toLowerCase(Locale.ROOT);
        var dashIndex = lang.indexOf('-');
        return dashIndex > 0 ? lang.substring(0, dashIndex) : lang;
    }

    private record CacheEntry(Map<String, YamlHelpTextEntry> entries, Instant fetchedAt) {
    }
}
