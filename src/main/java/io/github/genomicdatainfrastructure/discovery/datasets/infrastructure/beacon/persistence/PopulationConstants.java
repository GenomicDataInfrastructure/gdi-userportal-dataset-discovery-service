// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.beacon.persistence;

import java.util.regex.Pattern;

public final class PopulationConstants {

    public static final Pattern POPULATION_PATTERN = Pattern.compile(
            "^(?:([A-Z]{2})(?:_([MF]))?|([MF]))$", Pattern.CASE_INSENSITIVE);

    public static final String MALE_CODE = "M";
    public static final String FEMALE_CODE = "F";

    public static final String PARAM_REFERENCE_NAME = "referenceName";
    public static final String PARAM_START = "start";
    public static final String PARAM_END = "end";
    public static final String PARAM_REFERENCE_BASES = "referenceBases";
    public static final String PARAM_ALTERNATE_BASES = "alternateBases";
    public static final String PARAM_ASSEMBLY_ID = "assemblyId";
    public static final String PARAM_SEX = "sex";
    public static final String PARAM_COUNTRY_OF_BIRTH = "countryOfBirth";

    private PopulationConstants() {
    }

    public static boolean hasValue(String str) {
        return str != null && !str.isBlank();
    }
}