// SPDX-FileCopyrightText: 2026 Health-RI
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.discovery.model.RetrievedDataset;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanFilterHelpTextsResponse;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanPackage;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanValueLabel;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

@Log
@ApplicationScoped
public class CkanDatasetHelpTextService {

    private static final String DEFAULT_DATASET_TYPE = "dataset";

    private static final Map<String, String> SCHEMING_FIELD_TO_DATASET_PROPERTY = Map.ofEntries(
            Map.entry("title_translated", "title"),
            Map.entry("notes_translated", "description"),
            Map.entry("tags_translated", "keywords"),
            Map.entry("contact", "contacts"),
            Map.entry("publisher", "publishers"),
            Map.entry("owner_org", "ownerOrg"),
            Map.entry("url", "url"),
            Map.entry("issued", "createdAt"),
            Map.entry("modified", "modifiedAt"),
            Map.entry("version", "version"),
            Map.entry("version_notes", "versionNotes"),
            Map.entry("identifier", "identifier"),
            Map.entry("frequency", "frequency"),
            Map.entry("provenance", "provenance"),
            Map.entry("dcat_type", "dcatType"),
            Map.entry("temporal_coverage", "temporalCoverage"),
            Map.entry("temporal_resolution", "temporalResolution"),
            Map.entry("spatial_coverage", "spatialCoverage"),
            Map.entry("spatial_resolution_in_meters", "spatialResolutionInMeters"),
            Map.entry("access_rights", "accessRights"),
            Map.entry("alternate_identifier", "alternateIdentifier"),
            Map.entry("theme", "themes"),
            Map.entry("language", "languages"),
            Map.entry("documentation", "documentation"),
            Map.entry("conforms_to", "conformsTo"),
            Map.entry("is_referenced_by", "isReferencedBy"),
            Map.entry("distribution", "distributions"),
            Map.entry("sample", "samples"),
            Map.entry("analytics", "analytics"),
            Map.entry("applicable_legislation", "applicableLegislation"),
            Map.entry("has_version", "hasVersions"),
            Map.entry("code_values", "codeValues"),
            Map.entry("coding_system", "codingSystem"),
            Map.entry("purpose", "purpose"),
            Map.entry("health_category", "healthCategory"),
            Map.entry("health_theme", "healthTheme"),
            Map.entry("legal_basis", "legalBasis"),
            Map.entry("min_typical_age", "minTypicalAge"),
            Map.entry("max_typical_age", "maxTypicalAge"),
            Map.entry("number_of_records", "numberOfRecords"),
            Map.entry("number_of_unique_individuals", "numberOfUniqueIndividuals"),
            Map.entry("personal_data", "personalData"),
            Map.entry("trusted_data_holder", "trustedDataHolder"),
            Map.entry("population_coverage", "populationCoverage"),
            Map.entry("retention_period", "retentionPeriod"),
            Map.entry("hdab", "hdab"),
            Map.entry("qualified_relation", "qualifiedRelation"),
            Map.entry("provenance_activity", "provenanceActivity"),
            Map.entry("qualified_attribution", "qualifiedAttribution"),
            Map.entry("quality_annotation", "qualityAnnotation"),
            Map.entry("uri", "uri")
    );

    private final CkanQueryApi ckanQueryApi;
    private final ObjectMapper objectMapper;

    public CkanDatasetHelpTextService(@RestClient CkanQueryApi ckanQueryApi,
            ObjectMapper objectMapper) {
        this.ckanQueryApi = ckanQueryApi;
        this.objectMapper = objectMapper;
    }

    public RetrievedDataset enrich(RetrievedDataset dataset, CkanPackage ckanPackage,
            String preferredLanguage) {
        if (dataset == null || ckanPackage == null) {
            return dataset;
        }

        var helpTexts = retrieveHelpTexts(ckanPackage, preferredLanguage);
        if (!helpTexts.isEmpty()) {
            dataset.setHelpText(helpTexts);
        }
        return dataset;
    }

    private Map<String, String> retrieveHelpTexts(CkanPackage ckanPackage,
            String preferredLanguage) {
        try {
            return mapToDatasetProperties(Optional.ofNullable(ckanQueryApi.gdiDatasetHelpTextsShow(
                    preferredLanguage,
                    schemingType(ckanPackage),
                    keysAsJson()
            ))
                    .map(CkanFilterHelpTextsResponse::getResult)
                    .orElseGet(Map::of));
        } catch (RuntimeException exception) {
            log.log(Level.WARNING, "Could not retrieve CKAN dataset help texts", exception);
            return Map.of();
        }
    }

    private Map<String, String> mapToDatasetProperties(Map<String, String> helpTexts) {
        var mappedHelpTexts = new LinkedHashMap<String, String>();
        helpTexts.forEach((fieldName, helpText) -> {
            var datasetProperty = SCHEMING_FIELD_TO_DATASET_PROPERTY.get(fieldName);
            if (datasetProperty != null && StringUtils.isNotBlank(helpText)) {
                mappedHelpTexts.put(datasetProperty, helpText);
            }
        });
        return mappedHelpTexts;
    }

    private String keysAsJson() {
        try {
            return objectMapper.writeValueAsString(SCHEMING_FIELD_TO_DATASET_PROPERTY.keySet());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize dataset help-text keys for CKAN",
                    exception);
        }
    }

    private String schemingType(CkanPackage ckanPackage) {
        return Optional.ofNullable(ckanPackage.getType())
                .map(this::typeName)
                .filter(StringUtils::isNotBlank)
                .orElse(DEFAULT_DATASET_TYPE);
    }

    private String typeName(CkanValueLabel type) {
        return firstNotBlank(type.getName(), type.getDisplayName())
                .orElse(DEFAULT_DATASET_TYPE);
    }

    private Optional<String> firstNotBlank(String... values) {
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return Optional.of(value.trim());
            }
        }
        return Optional.empty();
    }
}
