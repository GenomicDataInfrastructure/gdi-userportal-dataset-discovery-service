// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

// TODO review original field and date format on resources
@UtilityClass
public class CkanDatetimeParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    public OffsetDateTime datetime(String date) {
        if (date == null) {
            return null;
        }

        try {
            return OffsetDateTime.parse(date);
        } catch (DateTimeParseException e) {
            var dateToParse = date;
            if (dateToParse.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$")) {
                dateToParse += "T00:00:00.000000";
            }
            return LocalDateTime.parse(dateToParse, DATE_FORMATTER)
                    .truncatedTo(ChronoUnit.SECONDS)
                    .atOffset(ZoneOffset.UTC);
        }
    }
}
