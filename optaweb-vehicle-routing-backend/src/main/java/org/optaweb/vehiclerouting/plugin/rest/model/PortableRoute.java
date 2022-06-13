package org.optaweb.vehiclerouting.plugin.rest.model;

import java.util.List;
import java.util.Objects;

import org.optaweb.vehiclerouting.domain.Route;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Vehicle {@link Route route} representation convenient for marshalling.
 */
class PortableRoute {

    private final PortableVehicle vehicle;
    private final PortableLocation depot;
    private final List<PortableLocation> visits;
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private final List<List<PortableCoordinates>> track;

    PortableRoute(
            PortableVehicle vehicle,
            PortableLocation depot,
            List<PortableLocation> visits,
            List<List<PortableCoordinates>> track) {
        this.vehicle = Objects.requireNonNull(vehicle);
        this.depot = Objects.requireNonNull(depot);
        this.visits = Objects.requireNonNull(visits);
        this.track = Objects.requireNonNull(track);
    }

    public PortableVehicle getVehicle() {
        return vehicle;
    }

    public PortableLocation getDepot() {
        return depot;
    }

    public List<PortableLocation> getVisits() {
        return visits;
    }

    public List<List<PortableCoordinates>> getTrack() {
        return track;
    }
}
