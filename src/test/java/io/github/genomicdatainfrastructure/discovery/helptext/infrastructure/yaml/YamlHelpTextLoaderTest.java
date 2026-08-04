// SPDX-FileCopyrightText: 2026 Health-RI
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.helptext.infrastructure.yaml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

class YamlHelpTextLoaderTest {

    private static class MutableClock extends Clock {

        private Instant instant;

        MutableClock(Instant instant) {
            this.instant = instant;
        }

        void advance(Duration duration) {
            instant = instant.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }

    private static void write(Path path, String content) throws IOException {
        Files.writeString(path, content);
    }

    @Test
    void lookupResolvesRequestedLanguage(@TempDir Path tempDir) throws IOException {
        var file = tempDir.resolve("filters.yaml");
        write(file, """
                access_rights:
                  text:
                    en: "English text"
                    nl: "Dutch text"
                """);
        var loader = new YamlHelpTextLoader(Clock.systemUTC());

        var nl = loader.lookup(file.toString(), Duration.ofMinutes(5), "access_rights", "nl");
        var en = loader.lookup(file.toString(), Duration.ofMinutes(5), "access_rights", "en");

        assertThat(nl).isPresent();
        assertThat(nl.get().getText()).isEqualTo("Dutch text");
        assertThat(en.get().getText()).isEqualTo("English text");
    }

    @Test
    void lookupFallsBackToEnglishWhenLanguageMissing(@TempDir Path tempDir) throws IOException {
        var file = tempDir.resolve("filters.yaml");
        write(file, """
                access_rights:
                  text:
                    en: "English only"
                """);
        var loader = new YamlHelpTextLoader(Clock.systemUTC());

        var result = loader.lookup(file.toString(), Duration.ofMinutes(5), "access_rights",
                "nl");

        assertThat(result).isPresent();
        assertThat(result.get().getText()).isEqualTo("English only");
    }

    @Test
    void lookupReturnsEmptyLinkWhenNoLinkPresent(@TempDir Path tempDir) throws IOException {
        var file = tempDir.resolve("filters.yaml");
        write(file, """
                access_rights:
                  text:
                    en: "English text"
                """);
        var loader = new YamlHelpTextLoader(Clock.systemUTC());

        var result = loader.lookup(file.toString(), Duration.ofMinutes(5), "access_rights",
                "en");

        assertThat(result).isPresent();
        assertThat(result.get().getLink().getLabel()).isEmpty();
        assertThat(result.get().getLink().getValue()).isEmpty();
    }

    @Test
    void lookupAcceptsLanguageTaggedListLabel(@TempDir Path tempDir) throws IOException {
        var file = tempDir.resolve("filters.yaml");
        write(file, """
                access_rights:
                  text:
                    en: "English text"
                  link:
                    label:
                      en: ["Label one", "Label two"]
                    value:
                      - "https://example.com/one"
                      - "https://example.com/two"
                """);
        var loader = new YamlHelpTextLoader(Clock.systemUTC());

        var result = loader.lookup(file.toString(), Duration.ofMinutes(5), "access_rights", "en")
                .orElseThrow();

        assertThat(result.getLink().getLabel()).containsExactly("Label one", "Label two");
    }

    @Test
    void lookupAcceptsLanguageTaggedSingleStringLabel(@TempDir Path tempDir) throws IOException {
        var file = tempDir.resolve("filters.yaml");
        write(file, """
                access_rights:
                  text:
                    en: "English text"
                  link:
                    label:
                      en: "English label"
                      nl: "Dutch label"
                    value:
                      - "https://example.com"
                """);
        var loader = new YamlHelpTextLoader(Clock.systemUTC());

        var en = loader.lookup(file.toString(), Duration.ofMinutes(5), "access_rights", "en")
                .orElseThrow();
        var nl = loader.lookup(file.toString(), Duration.ofMinutes(5), "access_rights", "nl")
                .orElseThrow();

        assertThat(en.getLink().getLabel()).containsExactly("English label");
        assertThat(nl.getLink().getLabel()).containsExactly("Dutch label");
    }

    @Test
    void lookupPadsShorterLabelOrValueListWithDummyEntries(
            @TempDir Path tempDir) throws IOException {
        var file = tempDir.resolve("filters.yaml");
        write(file, """
                access_rights:
                  text:
                    en: "English text"
                  link:
                    label:
                      en: ["Only one label"]
                    value:
                      - "https://example.com/one"
                      - "https://example.com/two"
                """);
        var loader = new YamlHelpTextLoader(Clock.systemUTC());

        var result = loader.lookup(file.toString(), Duration.ofMinutes(5), "access_rights",
                "en").orElseThrow();

        assertThat(result.getLink().getValue())
                .containsExactly("https://example.com/one", "https://example.com/two");
        assertThat(result.getLink().getLabel())
                .containsExactly("Only one label", "More info");
    }

    @Test
    void lookupReturnsEmptyWhenKeyNotFound(@TempDir Path tempDir) throws IOException {
        var file = tempDir.resolve("filters.yaml");
        write(file, """
                access_rights:
                  text:
                    en: "English text"
                """);
        var loader = new YamlHelpTextLoader(Clock.systemUTC());

        var result = loader.lookup(file.toString(), Duration.ofMinutes(5), "theme", "en");

        assertThat(result).isEmpty();
    }

    @Test
    void lookupCachesParsedContentUntilTtlExpires(@TempDir Path tempDir) throws IOException {
        var file = tempDir.resolve("filters.yaml");
        write(file, """
                access_rights:
                  text:
                    en: "Version 1"
                """);
        var clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        var loader = new YamlHelpTextLoader(clock);
        var ttl = Duration.ofMinutes(5);

        var first = loader.lookup(file.toString(), ttl, "access_rights", "en").orElseThrow();
        assertThat(first.getText()).isEqualTo("Version 1");

        write(file, """
                access_rights:
                  text:
                    en: "Version 2"
                """);

        var stillCached = loader.lookup(file.toString(), ttl, "access_rights", "en")
                .orElseThrow();
        assertThat(stillCached.getText()).isEqualTo("Version 1");

        clock.advance(Duration.ofMinutes(6));

        var refreshed = loader.lookup(file.toString(), ttl, "access_rights", "en").orElseThrow();
        assertThat(refreshed.getText()).isEqualTo("Version 2");
    }

    @Test
    void lookupServesStaleCacheWhenSourceBecomesUnreadable(
            @TempDir Path tempDir) throws IOException {
        var file = tempDir.resolve("filters.yaml");
        write(file, """
                access_rights:
                  text:
                    en: "Last known good"
                """);
        var clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        var loader = new YamlHelpTextLoader(clock);
        var ttl = Duration.ofMinutes(5);

        var first = loader.lookup(file.toString(), ttl, "access_rights", "en").orElseThrow();
        assertThat(first.getText()).isEqualTo("Last known good");

        Files.delete(file);
        clock.advance(Duration.ofMinutes(6));

        var afterFailure = loader.lookup(file.toString(), ttl, "access_rights", "en")
                .orElseThrow();
        assertThat(afterFailure.getText()).isEqualTo("Last known good");
    }

    @Test
    void lookupReturnsEmptyForBlankLocation() {
        var loader = new YamlHelpTextLoader(Clock.systemUTC());

        var result = loader.lookup(" ", Duration.ofMinutes(5), "access_rights", "en");

        assertThat(result).isEmpty();
    }

    @Test
    void lookupReturnsEmptyWhenLocationDoesNotExist(@TempDir Path tempDir) {
        var loader = new YamlHelpTextLoader(Clock.systemUTC());
        var missing = tempDir.resolve("does-not-exist.yaml");

        Optional<?> result = loader.lookup(missing.toString(), Duration.ofMinutes(5),
                "access_rights", "en");

        assertThat(result).isEmpty();
    }
}
