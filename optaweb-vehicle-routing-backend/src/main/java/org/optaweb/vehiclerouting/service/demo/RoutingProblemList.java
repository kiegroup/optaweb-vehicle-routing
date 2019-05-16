/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaweb.vehiclerouting.service.demo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaweb.vehiclerouting.domain.RoutingProblem;

/**
 * Utility class that holds a map of routing problem instances and allows to look them up by name.
 */
class RoutingProblemList {

    private final Map<String, RoutingProblem> routingProblems;

    RoutingProblemList(List<RoutingProblem> routingProblems) {
        this.routingProblems = Objects.requireNonNull(routingProblems).stream()
                // TODO use file name as the key (that's more likely to be unique than data set name)
                .collect(Collectors.toMap(RoutingProblem::name, Function.identity()));
    }

    Collection<RoutingProblem> all() {
        return routingProblems.values();
    }

    RoutingProblem byName(String name) {
        RoutingProblem routingProblem = routingProblems.get(name);
        if (routingProblem == null) {
            throw new IllegalArgumentException("Data set with name '" + name + "' doesn't exist");
        }
        return routingProblem;
    }
}
