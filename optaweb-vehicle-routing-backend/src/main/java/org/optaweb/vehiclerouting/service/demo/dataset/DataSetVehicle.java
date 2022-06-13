package org.optaweb.vehiclerouting.service.demo.dataset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data set vehicle.
 */
public class DataSetVehicle {

    @JsonProperty
    final String name;
    @JsonProperty
    final int capacity;

    @JsonCreator
    public DataSetVehicle(@JsonProperty("name") String name, @JsonProperty("capacity") int capacity) {
        this.name = name;
        this.capacity = capacity;
    }
}
