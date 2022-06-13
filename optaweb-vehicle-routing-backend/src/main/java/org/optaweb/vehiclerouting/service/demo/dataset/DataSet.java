package org.optaweb.vehiclerouting.service.demo.dataset;

import java.util.List;

/**
 * Data set representation used for marshalling and unmarshalling.
 */
class DataSet {

    private String name;
    private List<DataSetVehicle> vehicles;
    private DataSetLocation depot;
    private List<DataSetLocation> visits;

    /**
     * Data set name (a short description).
     *
     * @return data set name (may be {@code null})
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Vehicles.
     *
     * @return vehicles (may be {@code null})
     */
    public List<DataSetVehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<DataSetVehicle> vehicles) {
        this.vehicles = vehicles;
    }

    /**
     * The depot.
     *
     * @return the depot (may be {@code null})
     */
    public DataSetLocation getDepot() {
        return depot;
    }

    public void setDepot(DataSetLocation depot) {
        this.depot = depot;
    }

    /**
     * Visits.
     *
     * @return visits (may be {@code null})
     */
    public List<DataSetLocation> getVisits() {
        return visits;
    }

    public void setVisits(List<DataSetLocation> visits) {
        this.visits = visits;
    }
}
