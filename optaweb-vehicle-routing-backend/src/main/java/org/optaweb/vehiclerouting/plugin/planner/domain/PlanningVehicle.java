package org.optaweb.vehiclerouting.plugin.planner.domain;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.optaplanner.core.api.domain.lookup.PlanningId;

public class PlanningVehicle implements Standstill {

    @PlanningId
    private long id;
    private int capacity;
    private PlanningDepot depot;

    // Shadow variables
    private PlanningVisit nextVisit;

    PlanningVehicle() {
        // Hide public constructor in favor of the factory.
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public PlanningDepot getDepot() {
        return depot;
    }

    public void setDepot(PlanningDepot depot) {
        this.depot = depot;
    }

    @Override
    public PlanningVisit getNextVisit() {
        return nextVisit;
    }

    @Override
    public void setNextVisit(PlanningVisit nextVisit) {
        this.nextVisit = nextVisit;
    }

    public Iterable<PlanningVisit> getFutureVisits() {
        return () -> new Iterator<PlanningVisit>() {
            PlanningVisit nextVisit = getNextVisit();

            @Override
            public boolean hasNext() {
                return nextVisit != null;
            }

            @Override
            public PlanningVisit next() {
                if (nextVisit == null) {
                    throw new NoSuchElementException();
                }
                PlanningVisit out = nextVisit;
                nextVisit = nextVisit.getNextVisit();
                return out;
            }
        };
    }

    @Override
    public PlanningLocation getLocation() {
        return depot.getLocation();
    }

    @Override
    public String toString() {
        return "PlanningVehicle{" +
                "capacity=" + capacity +
                (depot == null ? "" : ",depot=" + depot.getId()) +
                (nextVisit == null ? "" : ",nextVisit=" + nextVisit.getId()) +
                ",id=" + id +
                '}';
    }
}
