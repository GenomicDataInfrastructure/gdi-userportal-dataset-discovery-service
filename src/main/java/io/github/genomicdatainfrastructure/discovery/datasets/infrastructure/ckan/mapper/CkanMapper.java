// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.mapper;

import io.github.genomicdatainfrastructure.discovery.model.RetrievedDataset;
import io.github.genomicdatainfrastructure.discovery.model.RetrievedDistribution;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;
import io.github.genomicdatainfrastructure.discovery.model.ValueLabel;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mapstruct.CollectionMappingStrategy.ADDER_PREFERRED;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

@Mapper(componentModel = "jakarta", nullValueCheckStrategy = ALWAYS, nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, collectionMappingStrategy = ADDER_PREFERRED)
public interface CkanMapper {

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    @Mapping(target = "description", source = "notes")
    @Mapping(target = "themes", source = "theme")
    @Mapping(target = "contacts", source = "contact")
    @Mapping(target = "distributions", source = "resources")
    @Mapping(target = "keywords", source = "tags")
    @Mapping(target = "spatial", source = "spatialUri")
    @Mapping(target = "createdAt", source = "issued")
    @Mapping(target = "modifiedAt", source = "modified")
    @Mapping(target = "creators", source = "creator")
    @Mapping(target = "hasVersions", source = "hasVersion")
    @Mapping(target = "publishers", source = "publisher")
    @Mapping(target = "languages", source = "language")
    @Mapping(target = "catalogue", ignore = true)
    RetrievedDataset map(CkanPackage ckanPackage);

    @Mapping(target = "label", source = "displayName")
    @Mapping(target = "value", source = "name")
    @Mapping(target = "count", ignore = true)
    ValueLabel map(CkanValueLabel ckanValueLabel);

    @Mapping(target = "label", source = "displayName")
    @Mapping(target = "value", source = "name")
    @Mapping(target = "count", ignore = true)
    ValueLabel map(CkanTag ckanTag);

    @Mapping(target = "title", source = "name")
    @Mapping(target = "createdAt", source = "issuedDate")
    @Mapping(target = "modifiedAt", source = "modifiedDate")
    @Mapping(target = "languages", source = "language")
    RetrievedDistribution map(CkanResource ckanResource);

    default List<SearchedDataset> map(PackagesSearchResult result) {
        if (result == null || result.getResults() == null) {
            return Collections.emptyList();
        }
        return result.getResults().stream()
                .map(this::mapToSearchedDataset)
                .toList();
    }

    @Mapping(target = "description", source = "notes")
    @Mapping(target = "themes", source = "theme")
    @Mapping(target = "publishers", source = "publisher")
    @Mapping(target = "keywords", source = "tags")
    @Mapping(target = "modifiedAt", source = "modified")
    @Mapping(target = "createdAt", source = "issued")
    @Mapping(target = "distributionsCount", expression = "java(ckanPackage.getResources()!= null ? ckanPackage.getResources().size():0)")
    @Mapping(target = "catalogue", ignore = true)
    @Mapping(target = "recordsCount", ignore = true)
    SearchedDataset mapToSearchedDataset(CkanPackage ckanPackage);

    default OffsetDateTime map(String date) {
        if (StringUtils.isBlank(date)) {
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
