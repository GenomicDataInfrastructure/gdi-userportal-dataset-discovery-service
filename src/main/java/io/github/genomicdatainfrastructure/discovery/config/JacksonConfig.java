// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.deserializer.CkanValueLabelDeserializer;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQueryFilter;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanValueLabel;
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
 */
@Singleton
public class JacksonConfig implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper objectMapper) {
        // CKAN may return a value-label either as an object or as a raw string URI.
        var ckanModule = new SimpleModule();
        ckanModule.addDeserializer(CkanValueLabel.class, new CkanValueLabelDeserializer());
        objectMapper.registerModule(ckanModule);

        // Accept single values for collection fields when CKAN sends a string instead of an array
        // (e.g. contact.url as "https://..." instead of ["https://..."])
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        // For Collection types, coerce empty objects {} to empty lists
        // This handles cases where CKAN returns {} for array fields like temporal_coverage
        objectMapper.coercionConfigFor(LogicalType.Collection)
                .setCoercion(CoercionInputShape.EmptyObject, CoercionAction.AsEmpty);

        // For POJO types, coerce empty strings to null
        // This handles cases where CKAN returns "" instead of {} or null
        objectMapper.coercionConfigFor(LogicalType.POJO)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);

        // For Map types, coerce empty strings to empty maps
        // This handles multilingual fields like name_translated
        objectMapper.coercionConfigFor(LogicalType.Map)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty);

        // Beacon dropdown filters should not serialize optional fields as null.
        objectMapper.configOverride(BeaconRequestQueryFilter.class).setInclude(
                JsonInclude.Value.construct(JsonInclude.Include.NON_NULL,
                        JsonInclude.Include.NON_NULL));
    }
}
