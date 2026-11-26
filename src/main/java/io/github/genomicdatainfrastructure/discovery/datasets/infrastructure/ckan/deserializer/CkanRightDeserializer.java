// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanRight;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Reads a rights statement of a distribution. The gdi_current profile holds one text, so CKAN sends
 * a plain string. HealthDCAT-AP R8 holds a list of statements, each with an optional IRI and a
 * label, so CKAN sends objects. Both become a {@link CkanRight}, a blank one becomes {@code null}.
 */
public class CkanRightDeserializer extends JsonDeserializer<CkanRight> {

    @Override
    public CkanRight deserialize(JsonParser parser,
            DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);

        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }

        if (!node.isObject()) {
            return of(null, CkanLocalizedTextDeserializer.firstText(node));
        }

        var uri = CkanLocalizedTextDeserializer.firstText(node.get("uri"));
        var label = StringUtils.firstNonBlank(
                CkanLocalizedTextDeserializer.firstText(node.get("label")),
                CkanLocalizedTextDeserializer.firstText(node.get("label_translated")));

        return of(uri, label);
    }

    private static CkanRight of(String uri, String label) {
        if (uri == null && label == null) {
            return null;
        }

        return CkanRight.builder()
                .uri(uri)
                .label(label)
                .build();
    }
}
