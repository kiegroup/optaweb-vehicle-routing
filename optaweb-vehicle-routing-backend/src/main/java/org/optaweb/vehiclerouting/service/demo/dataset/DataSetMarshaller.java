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
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.springframework.stereotype.Component;

@Component
public class DataSetMarshaller {

    static final long NO_ID = 0;
    private final ObjectMapper mapper;

    public DataSetMarshaller() {
        mapper = new ObjectMapper(new YAMLFactory());
    }

    public DataSetMarshaller(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public RoutingProblem unmarshall(Reader reader) {
        return toDomain(unmarshallToDataSet(reader));
    }

    public String marshall(RoutingProblem routingProblem) {
        return marshall(toDataSet(routingProblem));
    }

    DataSet unmarshallToDataSet(Reader reader) {
        try {
            return mapper.readValue(reader, DataSet.class);
        } catch (IOException e) {
            throw new IllegalStateException("Can't read demo data set.", e);
        }
    }

    String marshall(DataSet dataSet) {
        try {
            return mapper.writeValueAsString(dataSet);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to marshal data set (" + dataSet.getName() + ")", e);
        }
    }

    static DataSet toDataSet(RoutingProblem routingProblem) {
        DataSet dataSet = new DataSet();
        dataSet.setName(routingProblem.getName());
        dataSet.setDepot(toDataSet(routingProblem.getDepot()));
        dataSet.setVisits(routingProblem.getVisits().stream().map(DataSetMarshaller::toDataSet).collect(Collectors.toList()));
        return dataSet;
    }

    static DataSetLocation toDataSet(Location location) {
        return new DataSetLocation(
                location.getDescription(),
                location.getLatLng().getLatitude().doubleValue(),
                location.getLatLng().getLongitude().doubleValue()
        );
    }

    static RoutingProblem toDomain(DataSet dataSet) {
        return new RoutingProblem(
                dataSet.getName(),
                toDomain(dataSet.getDepot()),
                dataSet.getVisits().stream().map(DataSetMarshaller::toDomain).collect(Collectors.toList())
        );
    }

    static Location toDomain(DataSetLocation dataSetLocation) {
        return new Location(
                NO_ID,
                LatLng.valueOf(dataSetLocation.getLat(), dataSetLocation.getLng()),
                dataSetLocation.getLabel()
        );
    }
}
