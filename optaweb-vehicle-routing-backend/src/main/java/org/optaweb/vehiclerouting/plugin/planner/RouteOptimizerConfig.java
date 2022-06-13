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
