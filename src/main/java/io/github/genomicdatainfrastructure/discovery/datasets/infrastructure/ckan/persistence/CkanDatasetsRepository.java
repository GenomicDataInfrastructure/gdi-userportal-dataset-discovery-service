// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.persistence;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.model.*;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.api.CkanQueryApi;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.*;
import io.github.genomicdatainfrastructure.discovery.utils.CkanFacetsQueryBuilder;
import io.github.genomicdatainfrastructure.discovery.utils.DatasetOrganizationMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_FACET_GROUP;
import static io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.config.CkanConfiguration.CKAN_IDENTIFIER_FIELD;
import static java.util.Optional.ofNullable;

// TODO review original field and date format on resources
// TODO remove retrieved distributions from API
@ApplicationScoped
public class CkanDatasetsRepository implements DatasetsRepository {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    );

    private final CkanQueryApi ckanQueryApi;

    @Inject
    public CkanDatasetsRepository(
            @RestClient CkanQueryApi ckanQueryApi
    ) {
        this.ckanQueryApi = ckanQueryApi;
    }

    @Override
    public List<SearchedDataset> search(
            Set<String> datasetIds,
            String sort,
            Integer rows,
            Integer start,
            String accessToken) {

        if (datasetIds == null || datasetIds.isEmpty()) {
            return List.of();
        }

        var facets = datasetIds
                .stream()
                .map(id -> DatasetSearchQueryFacet
                        .builder()
                        .facetGroup(CKAN_FACET_GROUP)
                        .facet(CKAN_IDENTIFIER_FIELD)
                        .value(id)
                        .build())
                .toList();

        var facetsQuery = CkanFacetsQueryBuilder.buildFacetQuery(DatasetSearchQuery
                .builder()
                .facets(facets)
                .build());

        var request = new PackageSearchRequest(
                null,
                facetsQuery,
                sort,
                rows,
                start,
                null);

        var response = ckanQueryApi.packageSearch(
                accessToken,
                request
        );

        return results(response.getResult());
    }

    private List<SearchedDataset> results(PackagesSearchResult result) {
        var nonNullPackages = ofNullable(result)
                .map(PackagesSearchResult::getResults)
                .filter(ObjectUtils::isNotEmpty)
                .orElseGet(List::of);

        return nonNullPackages.stream()
                .map(this::result)
                .toList();
    }

    private SearchedDataset result(CkanPackage dataset) {
        var catalogue = ofNullable(dataset.getOrganization())
                .map(CkanOrganization::getTitle)
                .orElse(null);

        return SearchedDataset.builder()
                .id(dataset.getId())
                .identifier(dataset.getIdentifier())
                .title(dataset.getTitle())
                .description(dataset.getNotes())
                .themes(values(dataset.getTheme()))
                .keywords(keywords(dataset.getTags()))
                .catalogue(catalogue)
                .organization(DatasetOrganizationMapper.from(dataset.getOrganization()))
                .modifiedAt(parse(dataset.getModified()))
                .createdAt(parse(dataset.getIssued()))
                .distributions(distributions(dataset.getResources()))
                .build();
    }

    private List<ValueLabel> keywords(List<CkanTag> tags) {
        return ofNullable(tags)
                .orElseGet(List::of)
                .stream()
                .map(this::keyword)
                .toList();
    }

    private ValueLabel keyword(CkanTag ckanTag) {
        return ValueLabel.builder()
                .label(ckanTag.getDisplayName())
                .value(ckanTag.getName())
                .build();
    }

    private List<ValueLabel> values(List<CkanValueLabel> values) {
        return ofNullable(values)
                .orElseGet(List::of)
                .stream()
                .map(this::value)
                .filter(Objects::nonNull)
                .toList();
    }

    private ValueLabel value(CkanValueLabel value) {
        return ofNullable(value)
                .map(it -> ValueLabel.builder()
                        .value(it.getName())
                        .label(it.getDisplayName())
                        .build())
                .orElse(null);
    }

    private OffsetDateTime parse(String date) {
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

    private List<RetrievedDistribution> distributions(List<CkanResource> resources) {
        return ofNullable(resources)
                .orElseGet(List::of)
                .stream()
                .map(this::distribution)
                .toList();
    }

    private RetrievedDistribution distribution(CkanResource ckanResource) {
        return RetrievedDistribution.builder()
                .id(ckanResource.getId())
                .title(ckanResource.getName())
                .description(ckanResource.getDescription())
                .format(value(ckanResource.getFormat()))
                .createdAt(parse(ckanResource.getCreated()))
                .modifiedAt(parse(ckanResource.getLastModified()))
                .downloadUrl(ckanResource.getDownloadUrl())
                .accessUrl(ckanResource.getAccessUrl())
                .build();
    }
}
