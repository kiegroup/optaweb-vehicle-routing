package org.optaweb.vehiclerouting.service.distance;

public class DistanceCalculationException extends RuntimeException {

    public DistanceCalculationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DistanceCalculationException(String message) {
        super(message);
    }
}
