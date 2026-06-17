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
    private static final String DATASET_SERIES_TYPE = "dataset_series";

    private static final Map<String, List<String>> SCHEMING_FIELD_TO_DATASET_PROPERTY = Map
            .ofEntries(
                    Map.entry("title_translated", List.of("title")),
                    Map.entry("notes_translated", List.of("description")),
                    Map.entry("tags_translated", List.of("keywords")),
                    Map.entry("contact", List.of("contacts")),
                    Map.entry("publisher", List.of("publishers")),
                    Map.entry("owner_org", List.of("ownerOrg")),
                    Map.entry("url", List.of("url")),
                    Map.entry("issued", List.of("createdAt")),
                    Map.entry("modified", List.of("modifiedAt")),
                    Map.entry("version", List.of("version")),
                    Map.entry("version_notes", List.of("versionNotes")),
                    Map.entry("identifier", List.of("identifier")),
                    Map.entry("frequency", List.of("frequency")),
                    Map.entry("provenance", List.of("provenance")),
                    Map.entry("dcat_type", List.of("dcatType")),
                    Map.entry("temporal_coverage", List.of("temporalCoverage")),
                    Map.entry("temporal_resolution", List.of("temporalResolution")),
                    Map.entry("spatial_coverage", List.of("spatialCoverage")),
                    Map.entry("spatial_resolution_in_meters", List.of("spatialResolutionInMeters")),
                    Map.entry("access_rights", List.of("accessRights")),
                    Map.entry("alternate_identifier", List.of("alternateIdentifier")),
                    Map.entry("theme", List.of("themes")),
                    Map.entry("language", List.of("languages")),
                    Map.entry("documentation", List.of("documentation")),
                    Map.entry("conforms_to", List.of("conformsTo")),
                    Map.entry("is_referenced_by", List.of("isReferencedBy")),
                    Map.entry("distribution", List.of("distributions")),
                    Map.entry("sample", List.of("samples")),
                    Map.entry("analytics", List.of("analytics")),
                    Map.entry("applicable_legislation", List.of("applicableLegislation")),
                    Map.entry("has_version", List.of("hasVersions")),
                    Map.entry("code_values", List.of("codeValues")),
                    Map.entry("coding_system", List.of("codingSystem")),
                    Map.entry("purpose", List.of("purpose")),
                    Map.entry("health_category", List.of("healthCategory")),
                    Map.entry("health_theme", List.of("healthTheme")),
                    Map.entry("legal_basis", List.of("legalBasis")),
                    Map.entry("min_typical_age", List.of("minTypicalAge")),
                    Map.entry("max_typical_age", List.of("maxTypicalAge")),
                    Map.entry("number_of_records", List.of("numberOfRecords")),
                    Map.entry("number_of_unique_individuals", List.of("numberOfUniqueIndividuals")),
                    Map.entry("personal_data", List.of("personalData")),
                    Map.entry("trusted_data_holder", List.of("trustedDataHolder")),
                    Map.entry("population_coverage", List.of("populationCoverage")),
                    Map.entry("retention_period", List.of("retentionPeriod")),
                    Map.entry("hdab", List.of("hdab")),
                    Map.entry("qualified_relation", List.of("qualifiedRelation")),
                    Map.entry("provenance_activity", List.of("provenanceActivity")),
                    Map.entry("qualified_attribution", List.of("qualifiedAttribution")),
                    Map.entry("quality_annotation", List.of("qualityAnnotation")),
                    Map.entry("uri", List.of("uri")),
                    Map.entry("in_series", List.of("inSeries")),
                    Map.entry("resource_fields.name_translated",
                            List.of("distributions.title", "samples.title", "analytics.title")),
                    Map.entry("resource_fields.description_translated",
                            List.of("distributions.description", "samples.description",
                                    "analytics.description")),
                    Map.entry("resource_fields.format",
                            List.of("distributions.format", "samples.format", "analytics.format")),
                    Map.entry("resource_fields.download_url",
                            List.of("distributions.downloadUrl", "samples.downloadUrl",
                                    "analytics.downloadUrl")),
                    Map.entry("resource_fields.access_services", List.of(
                            "distributions.accessService")),
                    Map.entry("resource_fields.access_services.title",
                            List.of("distributions.accessService.title")),
                    Map.entry("resource_fields.access_services.description",
                            List.of("distributions.accessService.description"))
            );

    private static final Map<String, List<String>> SERIES_FIELD_TO_IN_SERIES_PROPERTY = Map
            .ofEntries(
                    Map.entry("title_translated", List.of("inSeries.title")),
                    Map.entry("notes_translated", List.of("inSeries.description")),
                    Map.entry("frequency", List.of("inSeries.frequency")),
                    Map.entry("temporal_coverage", List.of("inSeries.temporalCoverage")),
                    Map.entry("applicable_legislation", List.of("inSeries.applicableLegislation")),
                    Map.entry("contact", List.of("inSeries.contacts")),
                    Map.entry("publisher", List.of("inSeries.publishers")),
                    Map.entry("issued", List.of("inSeries.issued")),
                    Map.entry("modified", List.of("inSeries.modified")),
                    Map.entry("spatial_coverage", List.of("inSeries.spatial"))
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
        var helpTexts = new LinkedHashMap<String, String>();
        helpTexts.putAll(retrievePackageHelpTexts(ckanPackage, preferredLanguage));
        helpTexts.putAll(retrieveJoinedSeriesHelpTexts(ckanPackage, preferredLanguage));
        return helpTexts;
    }

    private Map<String, String> retrievePackageHelpTexts(CkanPackage ckanPackage,
            String preferredLanguage) {
        try {
            return mapToDatasetProperties(
                    Optional.ofNullable(ckanQueryApi.gdiDatasetHelpTextsShow(
                            preferredLanguage,
                            schemingType(ckanPackage),
                            keysAsJson(SCHEMING_FIELD_TO_DATASET_PROPERTY)
                    ))
                            .map(CkanFilterHelpTextsResponse::getResult)
                            .orElseGet(Map::of),
                    SCHEMING_FIELD_TO_DATASET_PROPERTY
            );
        } catch (RuntimeException exception) {
            log.log(Level.WARNING, "Could not retrieve CKAN dataset help texts", exception);
            return Map.of();
        }
    }

    private Map<String, String> retrieveJoinedSeriesHelpTexts(CkanPackage ckanPackage,
            String preferredLanguage) {
        if (ckanPackage.getInSeries() == null || ckanPackage.getInSeries().isEmpty()) {
            return Map.of();
        }

        try {
            return mapToDatasetProperties(
                    Optional.ofNullable(ckanQueryApi.gdiDatasetHelpTextsShow(
                            preferredLanguage,
                            DATASET_SERIES_TYPE,
                            keysAsJson(SERIES_FIELD_TO_IN_SERIES_PROPERTY)
                    ))
                            .map(CkanFilterHelpTextsResponse::getResult)
                            .orElseGet(Map::of),
                    SERIES_FIELD_TO_IN_SERIES_PROPERTY
            );
        } catch (RuntimeException exception) {
            log.log(Level.WARNING, "Could not retrieve CKAN dataset series help texts", exception);
            return Map.of();
        }
    }

    private Map<String, String> mapToDatasetProperties(Map<String, String> helpTexts,
            Map<String, List<String>> fieldMappings) {
        var mappedHelpTexts = new LinkedHashMap<String, String>();
        helpTexts.forEach((fieldName, helpText) -> {
            var datasetProperties = fieldMappings.get(fieldName);
            var normalizedHelpText = StringUtils.normalizeSpace(helpText);
            if (datasetProperties != null && StringUtils.isNotBlank(normalizedHelpText)) {
                datasetProperties.forEach(datasetProperty -> mappedHelpTexts.put(datasetProperty,
                        normalizedHelpText));
            }
        });
        return mappedHelpTexts;
    }

    private String keysAsJson(Map<String, List<String>> fieldMappings) {
        try {
            return objectMapper.writeValueAsString(fieldMappings.keySet());
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
