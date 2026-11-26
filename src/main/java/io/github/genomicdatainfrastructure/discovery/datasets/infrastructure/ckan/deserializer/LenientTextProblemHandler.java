// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps a dataset readable when a metadata profile serves a text field as a list or as an object.
 *
 * <p>
 * The CKAN deployment decides the shape of every field through its metadata profiles (gdi_current,
 * HealthDCAT-AP R8, ...), and another profile can make a text field a list or a text per language.
 * Without this handler one such field fails the whole dataset. With it the first non-blank text is
 * used (English first for a language map), and the field is reported once in the log so the model
 * can be widened on purpose. It only handles a text target: a list that a model declares as a list
 * needs no help, and other mismatches still fail.
 */
public class LenientTextProblemHandler extends DeserializationProblemHandler {

    private static final Logger LOG = Logger.getLogger(LenientTextProblemHandler.class);

    private final Set<String> reported = ConcurrentHashMap.newKeySet();

    @Override
    public Object handleUnexpectedToken(DeserializationContext context, JavaType targetType,
            JsonToken token, JsonParser parser, String failureMessage) throws IOException {
        if (!targetType.hasRawClass(String.class)
                || (token != JsonToken.START_ARRAY && token != JsonToken.START_OBJECT)) {
            return NOT_HANDLED;
        }

        var path = path(parser);
        var node = context.readTree(parser);

        if (reported.add(path)) {
            LOG.warnf("CKAN field '%s' is %s where the model expects a text; using its first text. "
                    + "Widen the CKAN model if the other values are needed.",
                    path, token == JsonToken.START_ARRAY ? "a list" : "an object");
        }

        return CkanLocalizedTextDeserializer.firstText(node);
    }

    /**
     * The property names that lead to the current value, without list positions, so the report is
     * once per field and not once per dataset.
     */
    private static String path(JsonParser parser) {
        var names = new ArrayDeque<String>();

        for (var context = parser.getParsingContext(); context != null; context = context
                .getParent()) {
            if (context.inObject() && context.getCurrentName() != null) {
                names.addFirst(context.getCurrentName());
            }
        }

        return String.join(".", names);
    }
}
