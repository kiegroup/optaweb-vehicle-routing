package org.optaweb.vehiclerouting.service.region;

import java.util.Objects;

import org.optaweb.vehiclerouting.domain.Coordinates;

/**
 * Bounding box.
 */
public class BoundingBox {

    private final Coordinates southWest;
    private final Coordinates northEast;

    /**
     * Create bounding box. The box must have non-zero dimensions and the corners must be south-west and north-east.
     *
     * @param southWest south-west corner (minimal latitude and longitude)
     * @param northEast north-east corner (maximal latitude and longitude)
     */
    public BoundingBox(Coordinates southWest, Coordinates northEast) {
        this.southWest = Objects.requireNonNull(southWest);
        this.northEast = Objects.requireNonNull(northEast);
        if (southWest.latitude().compareTo(northEast.latitude()) >= 0) {
            throw new IllegalArgumentException(
                    "South-west corner latitude ("
                            + southWest.latitude()
                            + "N) must be less than north-east corner latitude ("
                            + northEast.latitude()
                            + "N)");
        }
        if (southWest.longitude().compareTo(northEast.longitude()) >= 0) {
            throw new IllegalArgumentException(
                    "South-west corner longitude ("
                            + southWest.longitude()
                            + "E) must be less than north-east corner longitude ("
                            + northEast.longitude()
                            + "E)");
        }
    }

    /**
     * South-west corner of the bounding box.
     *
     * @return south-west corner (minimal latitude and longitude)
     */
    public Coordinates getSouthWest() {
        return southWest;
    }

    /**
     * North-east corner of the bounding box.
     *
     * @return north-east corner (maximal latitude and longitude)
     */
    public Coordinates getNorthEast() {
        return northEast;
    }
}
