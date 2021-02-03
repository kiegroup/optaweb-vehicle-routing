/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.acme.getting.started;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.RoutingPlan;
import org.optaweb.vehiclerouting.service.route.Router;
import org.optaweb.vehiclerouting.service.route.RoutingPlanConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class RouteListenerDependency implements Router, RoutingPlanConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RouteListenerDependency.class);

    @Override
    public List<Coordinates> getPath(Coordinates from, Coordinates to) {
        return Collections.emptyList();
    }

    @Override
    public void consumePlan(RoutingPlan routingPlan) {
        logger.info("New routing plan: {}", routingPlan);
    }
}
