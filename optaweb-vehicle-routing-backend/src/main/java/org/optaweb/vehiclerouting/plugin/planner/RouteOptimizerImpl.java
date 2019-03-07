/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Route;
import org.optaweb.vehiclerouting.plugin.planner.change.AddCustomer;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveCustomer;
import org.optaweb.vehiclerouting.plugin.planner.change.RemoveLocation;
import org.optaweb.vehiclerouting.service.location.DistanceMatrix;
import org.optaweb.vehiclerouting.service.location.RouteOptimizer;
import org.optaweb.vehiclerouting.service.route.RouteChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class RouteOptimizerImpl implements RouteOptimizer,
                                           SolverEventListener<VehicleRoutingSolution> {

    private static final Logger logger = LoggerFactory.getLogger(RouteOptimizerImpl.class);

    private final ApplicationEventPublisher publisher;
    private final Solver<VehicleRoutingSolution> solver;
    private final AsyncTaskExecutor executor;
    private Future<?> solverFuture;
    private VehicleRoutingSolution solution;

    @Autowired
    public RouteOptimizerImpl(ApplicationEventPublisher publisher,
                              Solver<VehicleRoutingSolution> solver,
                              AsyncTaskExecutor executor) {
        this.publisher = publisher;
        this.solver = solver;
        this.executor = executor;

        this.solver.addEventListener(this);
        solution = emptySolution();
    }

    public static VehicleRoutingSolution emptySolution() {
        VehicleRoutingSolution solution = new VehicleRoutingSolution();
        solution.setLocationList(new ArrayList<>());
        solution.setCustomerList(new ArrayList<>());
        solution.setDepotList(new ArrayList<>());
        solution.setVehicleList(Collections.singletonList(new Vehicle()));
        solution.getVehicleList().get(0).setId(1L);
        solution.setScore(HardSoftLongScore.ZERO);
        return solution;
    }

    static RoadLocation coreToPlanner(org.optaweb.vehiclerouting.domain.Location location) {
        return new RoadLocation(location.getId(),
                location.getLatLng().getLatitude().doubleValue(),
                location.getLatLng().getLongitude().doubleValue()
        );
    }

    private static Optional<List<org.optaweb.vehiclerouting.domain.Route>> extractRoute(VehicleRoutingSolution solution) {
        // TODO race condition, if a rest thread deletes that location in the middle of this method happening on the solver thread
        // TODO make sure that location is still in the repository
        // TODO maybe repair the solution OR ignore if it's inconsistent (log WARNING)
        Vehicle vehicle = solution.getVehicleList().get(0);
        Depot depot = vehicle.getDepot();
        if (depot == null) {
            return Optional.of(new ArrayList<>());
        }
        List<org.optaweb.vehiclerouting.domain.Location> visits = new ArrayList<>();
        addLocationToRoute(visits, depot.getLocation());
        for (Customer customer = vehicle.getNextCustomer(); customer != null; customer = customer.getNextCustomer()) {
            addLocationToRoute(visits, customer.getLocation());
        }
        return Optional.of(Collections.singletonList(new Route(visits)));
    }

    private static void addLocationToRoute(List<org.optaweb.vehiclerouting.domain.Location> route, Location location) {
        route.add(new org.optaweb.vehiclerouting.domain.Location(
                location.getId(),
                LatLng.valueOf(location.getLatitude(), location.getLongitude())
        ));
    }

    static void addDepot(VehicleRoutingSolution solution, Location location) {
        Depot depot = new Depot();
        depot.setId(location.getId());
        depot.setLocation(location);
        solution.getDepotList().add(depot);
        solution.getVehicleList().get(0).setDepot(depot);
        solution.getLocationList().add(location);
    }

    static void addCustomer(VehicleRoutingSolution solution, Location location) {
        Customer customer = new Customer();
        customer.setId(location.getId());
        customer.setLocation(location);
        solution.getCustomerList().add(customer);
        solution.getLocationList().add(location);
    }

    private void publishRoute(VehicleRoutingSolution solution) {
        extractRoute(solution).ifPresent(routes -> {
            logger.debug("New solution with\n"
                            + "  customers: {}\n"
                            + "  depots:    {}\n"
                            + "  vehicles:  {}\n"
                            + "Routes: {}",
                    solution.getCustomerList().size(),
                    solution.getDepotList().size(),
                    solution.getVehicleList().size(),
                    routes);
            String distanceString = solution.getDistanceString(new DecimalFormat("#,##0.00"));
            publisher.publishEvent(new RouteChangedEvent(this, distanceString, routes));
        });
    }

    private void startSolver() {
        if (solverFuture != null) {
            throw new IllegalStateException("Solver start has already been requested");
        }
        // TODO move this to @Async method?
        // TODO use ListenableFuture to react to solve() exceptions immediately?
        solverFuture = executor.submit(() -> {
            solver.solve(solution);
        });
    }

    boolean isSolving() {
        if (solverFuture == null) {
            return false;
        }
        assertSolverIsAlive();
        return true;
    }

    private void assertSolverIsAlive() {
        if (solverFuture.isDone()) {
            try {
                solverFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Solver has died", e);
            }
            throw new IllegalStateException("Solver has finished solving even though it operates in daemon mode.");
        }
    }

    void stopSolver() {
        if (solverFuture != null) {
            // TODO what happens if solver hasn't started yet (solve() is called asynchronously)
            solver.terminateEarly();
            // make sure solver has terminated and propagate exceptions
            try {
                solverFuture.get();
                solverFuture = null;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Failed to stop solver", e);
            }
        }
    }

    @Override
    public void bestSolutionChanged(BestSolutionChangedEvent<VehicleRoutingSolution> bestSolutionChangedEvent) {
        if (!bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()) {
            logger.info("Ignoring a new best solution that has some problem facts missing");
            return;
        }
        solution = bestSolutionChangedEvent.getNewBestSolution();
        publishRoute(solution);
    }

    @Override
    public void addLocation(org.optaweb.vehiclerouting.domain.Location coreLocation,
                            DistanceMatrix distanceMatrix) {
        RoadLocation location = coreToPlanner(coreLocation);
        DistanceMap distanceMap = new DistanceMap(coreLocation, distanceMatrix.getRow(coreLocation));
        location.setTravelDistanceMap(distanceMap);
        // Unfortunately can't start solver with an empty solution (see https://issues.jboss.org/browse/PLANNER-776)
        if (!isSolving()) {
            switch (solution.getLocationList().size()) {
                case 0:
                    addDepot(solution, location);
                    publishRoute(solution);
                    break;
                case 1:
                    addCustomer(solution, location);
                    startSolver();
                    break;
                default:
                    throw new IllegalStateException("Illegal number of locations when solver is not solving: "
                            + solution.getLocationList().size());
            }
        } else {
            solver.addProblemFactChange(new AddCustomer(location));
        }
    }

    @Override
    public void removeLocation(org.optaweb.vehiclerouting.domain.Location coreLocation) {
        Location location = coreToPlanner(coreLocation);
        if (!isSolving()) {
            if (solution.getLocationList().size() != 1) {
                throw new IllegalStateException("Impossible number of locations (" + solution.getLocationList().size()
                        + ") when solver is not solving.\n" + solution.getLocationList());
            }
            solution.getLocationList().remove(0);
            solution.getDepotList().remove(0);
            solution.getVehicleList().get(0).setDepot(null);
            publishRoute(solution);
        } else {
            if (solution.getDepotList().get(0).getLocation().getId().equals(location.getId())) {
                throw new UnsupportedOperationException("You can only remove depot if there are no customers.");
            }
            if (solution.getCustomerList().size() == 1) {
                // depot and 1 customer remaining
                stopSolver();
                solution.getCustomerList().remove(0);
                solution.getLocationList().removeIf(l -> l.getId().equals(location.getId()));
                solution.getVehicleList().get(0).setNextCustomer(null);
                publishRoute(solution);
            } else {
                solver.addProblemFactChanges(Arrays.asList(new RemoveCustomer(location), new RemoveLocation(location)));
            }
        }
    }

    @Override
    public void clear() {
        stopSolver();
        solution = emptySolution();
        publishRoute(solution);
    }
}
