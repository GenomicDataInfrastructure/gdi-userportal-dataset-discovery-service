// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.api;

import io.github.genomicdatainfrastructure.discovery.datasets.application.usecases.GVariantsQuery;
import io.github.genomicdatainfrastructure.discovery.model.GVariantSearchQuery;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GVariantsApiImpl implements GVariantsApi {

    private final SecurityIdentity identity;
    private final GVariantsQuery gVariantsQuery;

    @Override
    public Response searchGenomicVariants(GVariantSearchQuery gvariantSearchQuery) {
        return Response.ok(gVariantsQuery.execute(gvariantSearchQuery, accessToken())).build();
    }

    private String accessToken() {
        if (identity.isAnonymous()) {
            return null;
        }
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        return principal.getRawToken();
    }

}
