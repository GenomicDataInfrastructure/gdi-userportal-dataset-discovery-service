// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.discovery.datasets.infrastructure.ckan.utils;

import io.github.genomicdatainfrastructure.discovery.model.Agent;
import io.github.genomicdatainfrastructure.discovery.remote.ckan.model.CkanAgent;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;

import static java.util.Optional.ofNullable;

@UtilityClass
public class CkanAgentParser {

    public List<Agent> agents(List<CkanAgent> agents) {
        return ofNullable(agents)
                .orElseGet(List::of)
                .stream()
                .map(CkanAgentParser::agent)
                .filter(Objects::nonNull)
                .toList();
    }

    public Agent agent(CkanAgent agent) {
        if (agent == null) {
            return null;
        }

        return Agent.builder()
                .name(agent.getName())
                .email(agent.getEmail())
                .url(agent.getUrl())
                .uri(agent.getUri())
                .type(agent.getType())
                .identifier(agent.getIdentifier())
                .build();

    }

}
