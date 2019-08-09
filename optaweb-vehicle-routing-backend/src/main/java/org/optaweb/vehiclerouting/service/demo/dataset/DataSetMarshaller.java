/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.service.demo.dataset;

import java.io.IOException;
import java.io.Reader;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.LocationData;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.springframework.stereotype.Component;

/**
 * Data set marshaller using the YAML format.
 */
@Component
public class DataSetMarshaller {

    private final ObjectMapper mapper;

    /**
     * Create marshaller using the default object mapper, which is set up to use YAML format.
     */
    DataSetMarshaller() {
        mapper = new ObjectMapper(new YAMLFactory());
    }

    /**
     * Constructor for testing purposes.
     * @param mapper usually a mock object mapper
     */
    DataSetMarshaller(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Unmarshal routing problem from a reader.
     * @param reader a reader
     * @return routing problem
     */
    public RoutingProblem unmarshal(Reader reader) {
        // TODO throw a checked exception that will force the caller to handle the reading problem
        //      (e.g. a bad format) and report it to the user or log an error
        return toDomain(unmarshalToDataSet(reader));
    }

    /**
     * Marshal routing problem to string.
     * @param routingProblem routing problem
     * @return string containing the marshaled routing problem
     */
    public String marshal(RoutingProblem routingProblem) {
        return marshal(toDataSet(routingProblem));
    }

    DataSet unmarshalToDataSet(Reader reader) {
        try {
            return mapper.readValue(reader, DataSet.class);
        } catch (IOException e) {
            throw new IllegalStateException("Can't read demo data set", e);
        }
    }

    String marshal(DataSet dataSet) {
        try {
            return mapper.writeValueAsString(dataSet);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to marshal data set (" + dataSet.getName() + ")", e);
        }
    }

    static DataSet toDataSet(RoutingProblem routingProblem) {
        DataSet dataSet = new DataSet();
        dataSet.setName(routingProblem.name());
        dataSet.setDepot(routingProblem.depot().map(DataSetMarshaller::toDataSet).orElse(null));
        dataSet.setVisits(
                routingProblem.visits().stream()
                        .map(DataSetMarshaller::toDataSet)
                        .collect(Collectors.toList())
        );
        return dataSet;
    }

    static DataSetLocation toDataSet(LocationData locationData) {
        return new DataSetLocation(
                locationData.description(),
                locationData.coordinates().latitude().doubleValue(),
                locationData.coordinates().longitude().doubleValue()
        );
    }

    static RoutingProblem toDomain(DataSet dataSet) {
        return new RoutingProblem(
                dataSet.getName(),
                toDomain(dataSet.getDepot()),
                dataSet.getVisits().stream().map(DataSetMarshaller::toDomain).collect(Collectors.toList())
        );
    }

    static LocationData toDomain(DataSetLocation dataSetLocation) {
        return new LocationData(
                Coordinates.valueOf(dataSetLocation.getLatitude(), dataSetLocation.getLongitude()),
                dataSetLocation.getLabel()
        );
    }
}
