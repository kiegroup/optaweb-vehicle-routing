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

import java.time.Duration;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaweb.vehiclerouting.solver.VehicleRoutingSolution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

/**
 * Spring configuration that creates {@link RouteOptimizerImpl route optimizer}'s dependencies.
 */
@Configuration
class RouteOptimizerConfig {


    public static final String SOLVER_CONFIG
            = "org.optaweb.vehiclerouting.solver/vehicleRoutingSolverConfig.xml";

    private final OptimizerProperties optimizerProperties;

    @Autowired
    RouteOptimizerConfig(OptimizerProperties optimizerProperties) {
        this.optimizerProperties = optimizerProperties;
    }

    @Bean
    Solver<VehicleRoutingSolution> solver() {
        SolverFactory<VehicleRoutingSolution> sf = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        Duration timeout = optimizerProperties.getTimeout();
        sf.getSolverConfig().setTerminationConfig(new TerminationConfig().withSecondsSpentLimit(timeout.getSeconds()));
        sf.getSolverConfig().setDaemon(true);
        return sf.buildSolver();
    }

    @Bean
    AsyncTaskExecutor executor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setConcurrencyLimit(1);
        return executor;
    }
}
