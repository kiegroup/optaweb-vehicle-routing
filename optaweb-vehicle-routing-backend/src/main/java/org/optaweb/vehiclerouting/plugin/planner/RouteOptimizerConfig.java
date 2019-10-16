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
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaweb.vehiclerouting.plugin.planner.domain.VehicleRoutingSolution;
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

    static final String SOLVER_CONFIG = "org/optaweb/vehiclerouting/solver/vehicleRoutingSolverConfig.xml";

    private final OptimizerProperties optimizerProperties;

    @Autowired
    RouteOptimizerConfig(OptimizerProperties optimizerProperties) {
        this.optimizerProperties = optimizerProperties;
    }

    @Bean
    Solver<VehicleRoutingSolution> solver() {
        // Use context classloader to avoid ClassCastException during solution cloning:
        // https://stackoverflow.com/questions/52586747/classcastexception-occured-on-solver-solve
        // as recommended in
        // CHECKSTYLE:OFF
        // https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-devtools.html#using-boot-devtools-customizing-classload
        // CHECKSTYLE:ON
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        SolverConfig solverConfig = SolverConfig.createFromXmlResource(SOLVER_CONFIG, classLoader);
        Duration timeout = optimizerProperties.getTimeout();
        solverConfig.setTerminationConfig(new TerminationConfig().withSecondsSpentLimit(timeout.getSeconds()));
        solverConfig.setDaemon(true);
        return SolverFactory.<VehicleRoutingSolution>create(solverConfig).buildSolver();
    }

    @Bean
    AsyncTaskExecutor executor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setConcurrencyLimit(1);
        return executor;
    }
}
