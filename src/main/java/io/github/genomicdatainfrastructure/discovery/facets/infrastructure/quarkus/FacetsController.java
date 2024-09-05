package io.github.genomicdatainfrastructure.discovery.facets.infrastructure.quarkus;

import io.github.genomicdatainfrastructure.discovery.api.SearchFacetsQueryApi;
import io.github.genomicdatainfrastructure.discovery.facets.application.RetrieveFacetsQuery;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FacetsController implements SearchFacetsQueryApi {

    private final SecurityIdentity identity;
    private final RetrieveFacetsQuery query;

    @Override
    public Response retrieveSearchFacets() {
        var facets = query.execute(accessToken());
        return Response.ok(facets).build();
    }

    private String accessToken() {
        if (identity.isAnonymous()) {
            return null;
        }
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        return principal.getRawToken();
    }
}
