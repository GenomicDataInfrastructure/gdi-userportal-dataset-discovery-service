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
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.deserializer.CkanLocalizedTextDeserializer;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.deserializer.CkanRightDeserializer;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.deserializer.CkanValueLabelDeserializer;
import io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.deserializer.LenientTextProblemHandler;
import io.github.genomicdatainfrastructure.discovery.remote.beacon.individuals.model.BeaconRequestQueryFilter;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanLocalizedText;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanRight;
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
 *
 * The shape of a CKAN field also depends on the metadata profile of the CKAN
 * deployment (gdi_current, HealthDCAT-AP R8, ...): a profile can serve a list
 * where another serves one value. The CKAN model declares such a field as a
 * list, which reads one value as well (ACCEPT_SINGLE_VALUE_AS_ARRAY), and
 * the handler below keeps a dataset readable when a text field arrives as a
 * list or an object.
 */
@Singleton
public class JacksonConfig implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper objectMapper) {
        // CKAN may return a value-label either as an object or as a raw string URI.
        var ckanModule = new SimpleModule();
        ckanModule.addDeserializer(CkanValueLabel.class, new CkanValueLabelDeserializer());
        // A rights statement is a plain text in gdi_current and an object (IRI and label) in R8.
        ckanModule.addDeserializer(CkanRight.class, new CkanRightDeserializer());
        // A text that is a plain string or a text per language (the variables of R8).
        ckanModule.addDeserializer(CkanLocalizedText.class, new CkanLocalizedTextDeserializer());
        objectMapper.registerModule(ckanModule);

        // A text field that a profile serves as a list or an object degrades to its first text
        // instead of failing the whole dataset.
        objectMapper.addHandler(new LenientTextProblemHandler());

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
