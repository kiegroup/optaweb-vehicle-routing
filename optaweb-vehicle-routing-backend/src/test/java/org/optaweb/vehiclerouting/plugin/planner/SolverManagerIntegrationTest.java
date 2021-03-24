/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.plugin.planner;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory.solutionFromVisits;

import java.util.concurrent.Semaphore;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.optaweb.vehiclerouting.plugin.planner.domain.DistanceMap;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocationFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;
import org.optaweb.vehiclerouting.service.route.RouteChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(SolverTestProfile.class)
class SolverManagerIntegrationTest {

    @Inject
    SolverManager solverManager;
    @Inject
    RouteChangedEventSemaphore routeChangedEventSemaphore;

    private static DistanceMap mockDistanceMap() {
        return location -> 60;
    }

    @Test
    @Timeout(value = 60)
    void solver_should_be_in_daemon_mode() throws InterruptedException {
        PlanningVehicle vehicle = PlanningVehicleFactory.testVehicle(1);
        PlanningLocation depot = PlanningLocationFactory.testLocation(1, mockDistanceMap());
        PlanningLocation visit = PlanningLocationFactory.testLocation(2, mockDistanceMap());
        VehicleRoutingSolution solution = solutionFromVisits(
                singletonList(vehicle),
                new PlanningDepot(depot),
                singletonList(PlanningVisitFactory.fromLocation(visit)));
        solverManager.startSolver(solution);

        // Waits until the solution is initialized. There is only 1 possible step => no more than 1 RouteChangedEvent.
        routeChangedEventSemaphore.waitForRouteUpdate();
        // The best solution has been updated. We know the score must be -1hard/-120soft because that's
        // the only possible solution. The termination property is set exactly to this score => we know
        // the solver is now terminated.

        // If the solver is in daemon mode, it doesn't return from solve() although solving has ended.
        // Instead, it's actively waiting for a PFC and will restart once it arrives from the outside (the test thread).
        // If it's not in daemon mode, it returns from solve() method once the termination condition is met
        // and the following PFC attempt fails.
        assertThatCode(() -> solverManager.changeCapacity(vehicle)).doesNotThrowAnyException();
    }

    @ApplicationScoped
    static class RouteChangedEventSemaphore {

        private static final Logger logger = LoggerFactory.getLogger(RouteChangedEventSemaphore.class);
        private final Semaphore semaphore = new Semaphore(0);

        public void onApplicationEvent(@Observes RouteChangedEvent event) {
            logger.info("DISTANCE: {}", event.distance());
            semaphore.release();
        }

        void waitForRouteUpdate() throws InterruptedException {
            semaphore.acquire();
            int remainingPermits = semaphore.availablePermits();
            if (remainingPermits > 0) {
                throw new IllegalStateException(
                        "Only 1 RouteChangedEvent was expected but there were at least " + (remainingPermits + 1));
            }
        }
    }
}
