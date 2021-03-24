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

package org.optaweb.vehiclerouting.plugin.planner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Configuration bean that creates {@link RouteOptimizerImpl route optimizer}'s dependencies.
 */
@Dependent
class RouteOptimizerConfig {

    private final SolverFactory<VehicleRoutingSolution> solverFactory;

    RouteOptimizerConfig(SolverFactory<VehicleRoutingSolution> solverFactory) {
        this.solverFactory = solverFactory;
    }

    @Produces
    Solver<VehicleRoutingSolution> solver() {
        return solverFactory.buildSolver();
    }

    @Produces
    ListeningExecutorService executor() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        return MoreExecutors.listeningDecorator(executorService);
    }
}
