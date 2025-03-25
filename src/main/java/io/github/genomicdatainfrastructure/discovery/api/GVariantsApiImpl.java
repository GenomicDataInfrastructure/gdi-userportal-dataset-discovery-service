// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.datasets.application.usecases.GVariantsQuery;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GVariantsApiImpl implements GVariantsApi {

    private final GVariantsQuery gVariantsQuery;

    @Override
    public Response searchGenomicVariants(GVariantSearchQuery gvariantSearchQuery) {
        return Response.ok(gVariantsQuery.execute(gvariantSearchQuery)).build();
    }

}
