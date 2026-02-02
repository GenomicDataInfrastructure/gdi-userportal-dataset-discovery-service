// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackage;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

/**
 * Jackson ObjectMapper customizer to handle CKAN's inconsistent data format.
 *
 * CKAN sometimes returns empty strings "" instead of null or empty objects {}
 * for fields that are defined as objects in the schema (e.g., name_translated,
 * type in hdab, motivated_by in quality_annotation).
 *
 * This configuration allows Jackson to coerce empty strings to null for POJO
 * types, preventing deserialization errors.
 *
 * Additionally, it registers a mix-in for CkanPackage to handle the tags field
 * which CKAN returns in inconsistent formats (sometimes as strings, sometimes
 * as objects with name/display_name fields).
 */
@Singleton
public class JacksonConfig implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper objectMapper) {
        // For POJO types, coerce empty strings to null
        // This handles cases where CKAN returns "" instead of {} or null
        objectMapper.coercionConfigFor(LogicalType.POJO)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);

        // For Map types, coerce empty strings to empty maps
        // This handles multilingual fields like name_translated
        objectMapper.coercionConfigFor(LogicalType.Map)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty);

        // For Collection types (arrays/lists), coerce empty strings to empty collections
        // This handles array fields that CKAN returns as "" instead of []
        objectMapper.coercionConfigFor(LogicalType.Collection)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty);

        // Register mix-in for CkanPackage to handle tags field with both string and object formats
        objectMapper.addMixIn(CkanPackage.class, CkanPackageMixin.class);
    }
}
