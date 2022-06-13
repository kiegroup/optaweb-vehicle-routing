package org.optaweb.vehiclerouting.service.location;

import java.util.List;
import java.util.Optional;

import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;

/**
 * Defines repository operations on locations.
 */
public interface LocationRepository {

    /**
     * Create a location with a unique ID.
     *
     * @param coordinates location's coordinates
     * @param description description of the location
     * @return a new location
     */
    Location createLocation(Coordinates coordinates, String description);

    /**
     * Get all locations.
     *
     * @return all locations
     */
    List<Location> locations();

    /**
     * Remove a location with the given ID.
     *
     * @param id location ID
     * @return the removed location
     */
    Location removeLocation(long id);

    /**
     * Remove all locations from the repository.
     */
    void removeAll();

    /**
     * Find a location by its ID.
     *
     * @param locationId location's ID
     * @return an Optional containing location with the given ID or empty Optional if there is no location with such ID
     */
    Optional<Location> find(long locationId);
}
