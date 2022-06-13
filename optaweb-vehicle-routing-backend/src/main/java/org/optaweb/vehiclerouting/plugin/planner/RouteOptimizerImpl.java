package org.optaweb.vehiclerouting.plugin.planner;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.Vehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningDepot;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocation;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningLocationFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicle;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVehicleFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisit;
import org.optaweb.vehiclerouting.plugin.planner.domain.PlanningVisitFactory;
import org.optaweb.vehiclerouting.plugin.planner.domain.SolutionFactory;
import org.optaweb.vehiclerouting.service.location.DistanceMatrixRow;
import org.optaweb.vehiclerouting.service.location.LocationPlanner;
import org.optaweb.vehiclerouting.service.vehicle.VehiclePlanner;

/**
 * Accumulates vehicles, depots and visits until there's enough data to start the optimization.
 * Solutions are published even if solving hasn't started yet due to missing facts (e.g. no vehicles or no visits).
 * Stops solver when vehicles or visits are reduced to zero.
 */
@ApplicationScoped
class RouteOptimizerImpl implements LocationPlanner, VehiclePlanner {

    private final SolverManager solverManager;
    private final RouteChangedEventPublisher routeChangedEventPublisher;

    private final List<PlanningVehicle> vehicles = new ArrayList<>();
    private final List<PlanningVisit> visits = new ArrayList<>();
    private PlanningDepot depot;

    @Inject
    RouteOptimizerImpl(SolverManager solverManager, RouteChangedEventPublisher routeChangedEventPublisher) {
        this.solverManager = solverManager;
        this.routeChangedEventPublisher = routeChangedEventPublisher;
    }

    @Override
    public void addLocation(Location domainLocation, DistanceMatrixRow distanceMatrixRow) {
        PlanningLocation location = PlanningLocationFactory.fromDomain(
                domainLocation,
                new DistanceMapImpl(distanceMatrixRow));
        // Unfortunately can't start solver with an empty solution (see https://issues.redhat.com/browse/PLANNER-776)
        if (depot == null) {
            depot = new PlanningDepot(location);
            publishSolution();
        } else {
            PlanningVisit visit = PlanningVisitFactory.fromLocation(location);
            visits.add(visit);
            if (vehicles.isEmpty()) {
                publishSolution();
            } else if (visits.size() == 1) {
                solverManager.startSolver(SolutionFactory.solutionFromVisits(vehicles, depot, visits));
            } else {
                solverManager.addVisit(visit);
            }
        }
    }

    @Override
    public void removeLocation(Location domainLocation) {
        if (visits.isEmpty()) {
            if (depot == null) {
                throw new IllegalArgumentException(
                        "Cannot remove " + domainLocation + " because there are no locations");
            }
            if (depot.getId() != domainLocation.id()) {
                throw new IllegalArgumentException("Cannot remove " + domainLocation + " because it doesn't exist");
            }
            depot = null;
            publishSolution();
        } else {
            if (depot.getId() == domainLocation.id()) {
                throw new IllegalStateException("You can only remove depot if there are no visits");
            }
            if (!visits.removeIf(item -> item.getId() == domainLocation.id())) {
                throw new IllegalArgumentException("Cannot remove " + domainLocation + " because it doesn't exist");
            }
            if (vehicles.isEmpty()) { // solver is not running
                publishSolution();
            } else if (visits.isEmpty()) { // solver is running
                solverManager.stopSolver();
                publishSolution();
            } else {
                // TODO maybe allow removing location by ID (only require the necessary information)
                solverManager.removeVisit(
                        PlanningVisitFactory.fromLocation(PlanningLocationFactory.fromDomain(domainLocation)));
            }
        }
    }

    @Override
    public void addVehicle(Vehicle domainVehicle) {
        PlanningVehicle vehicle = PlanningVehicleFactory.fromDomain(domainVehicle);
        vehicle.setDepot(depot);
        vehicles.add(vehicle);
        if (visits.isEmpty()) {
            publishSolution();
        } else if (vehicles.size() == 1) {
            solverManager.startSolver(SolutionFactory.solutionFromVisits(vehicles, depot, visits));
        } else {
            solverManager.addVehicle(vehicle);
        }
    }

    @Override
    public void removeVehicle(Vehicle domainVehicle) {
        if (!vehicles.removeIf(vehicle -> vehicle.getId() == domainVehicle.id())) {
            throw new IllegalArgumentException("Cannot remove " + domainVehicle + " because it doesn't exist");
        }
        if (visits.isEmpty()) { // solver is not running
            publishSolution();
        } else if (vehicles.isEmpty()) { // solver is running
            solverManager.stopSolver();
            publishSolution();
        } else {
            solverManager.removeVehicle(PlanningVehicleFactory.fromDomain(domainVehicle));
        }
    }

    @Override
    public void changeCapacity(Vehicle domainVehicle) {
        PlanningVehicle vehicle = vehicles.stream()
                .filter(item -> item.getId() == domainVehicle.id())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot change capacity of " + domainVehicle + " because it doesn't exist"));
        vehicle.setCapacity(domainVehicle.capacity());
        if (!visits.isEmpty()) {
            solverManager.changeCapacity(vehicle);
        } else {
            publishSolution();
        }
    }

    @Override
    public void removeAllLocations() {
        solverManager.stopSolver();
        depot = null;
        visits.clear();
        publishSolution();
    }

    @Override
    public void removeAllVehicles() {
        solverManager.stopSolver();
        vehicles.clear();
        publishSolution();
    }

    private void publishSolution() {
        routeChangedEventPublisher.publishSolution(SolutionFactory.solutionFromVisits(vehicles, depot, visits));
    }
}
