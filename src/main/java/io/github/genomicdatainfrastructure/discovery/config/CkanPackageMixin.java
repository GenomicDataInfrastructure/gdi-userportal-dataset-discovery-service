// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * Jackson mix-in for CkanPackage to customize deserialization of the tags field.
 *
 * This mix-in is applied to the generated CkanPackage class to use our custom
 * CkanTagsDeserializer that handles both string and object formats for tags.
 */
public abstract class CkanPackageMixin {

    @JsonDeserialize(using = CkanTagsDeserializer.class)
    public abstract List<String> getTags();
}
