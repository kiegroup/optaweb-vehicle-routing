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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.RoutingProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.optaweb.vehiclerouting.service.demo.dataset.DataSetMarshaller.NO_ID;
import static org.optaweb.vehiclerouting.service.demo.dataset.DataSetMarshaller.toDataSet;
import static org.optaweb.vehiclerouting.service.demo.dataset.DataSetMarshaller.toDomain;

public class DataSetMarshallerTest {

    @Test
    public void unmarshall_data_set() throws IOException {
        DataSet dataSet;
        try (InputStream inputStream = DataSetMarshallerTest.class.getResourceAsStream("test-belgium.yaml")) {
            dataSet = new DataSetMarshaller().unmarshallToDataSet(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        }
        assertThat(dataSet).isNotNull();

        assertThat(dataSet.getName()).isEqualTo("Belgium test");
        assertThat(dataSet.getDepot()).isNotNull();
        assertThat(dataSet.getDepot().getLabel()).isEqualTo("Brussels");
        assertThat(dataSet.getDepot().getLat()).isEqualTo(50.85);
        assertThat(dataSet.getDepot().getLng()).isEqualTo(4.35);
        assertThat(dataSet.getVisits())
                .extracting("label")
                .containsExactlyInAnyOrder("Aalst", "Châtelet", "La Louvière", "Sint-Niklaas", "Ypres");
    }

    @Test
    public void marshall_data_set() {
        DataSet dataSet = new DataSet();
        String name = "Data set name";
        dataSet.setName(name);
        DataSetLocation depot = new DataSetLocation("Depot", -1.1, -9.9);
        DataSetLocation location1 = new DataSetLocation("Location 1", 1.0, 0.1);
        DataSetLocation location2 = new DataSetLocation("Location 2", 2.0, 0.2);
        dataSet.setDepot(depot);
        dataSet.setVisits(Arrays.asList(location1, location2));
        String yaml = new DataSetMarshaller().marshall(dataSet);
        assertThat(yaml)
                .contains("name: \"" + name)
                .contains(depot.getLabel(), location1.getLabel(), location2.getLabel());
    }

    @Test
    public void should_rethrow_exception_from_object_mapper() throws IOException {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.readValue(any(Reader.class), eq(DataSet.class))).thenThrow(IOException.class);
        assertThatIllegalStateException()
                .isThrownBy(() -> new DataSetMarshaller(objectMapper).unmarshallToDataSet(mock(Reader.class)))
                .withRootCauseExactlyInstanceOf(IOException.class);

        when(objectMapper.writeValueAsString(any(DataSet.class))).thenThrow(JsonProcessingException.class);
        assertThatIllegalStateException()
                .isThrownBy(() -> new DataSetMarshaller(objectMapper).marshall(new DataSet()))
                .withRootCauseExactlyInstanceOf(JsonProcessingException.class);
    }

    @Test
    public void location_conversion() {
        double lat = -1.0;
        double lng = 50.2;
        String description = "some location";

        // domain -> data set
        DataSetLocation dataSetLocation = toDataSet(new Location(5578, LatLng.valueOf(lat, lng), description));
        assertThat(dataSetLocation.getLat()).isEqualTo(lat);
        assertThat(dataSetLocation.getLng()).isEqualTo(lng);
        assertThat(dataSetLocation.getLabel()).isEqualTo(description);

        // data set -> domain
        Location domainLocation = toDomain(dataSetLocation);
        assertThat(domainLocation).isEqualTo(new Location(0, LatLng.valueOf(lat, lng), description));
    }

    @Test
    public void routing_problem_conversion() {
        Location depot = new Location(NO_ID, LatLng.valueOf(60.1, 5.78), "Depot");
        Location visit = new Location(NO_ID, LatLng.valueOf(1.06, 8.75), "Visit");
        List<Location> visits = Arrays.asList(visit);
        String name = "some data set";

        // domain -> data set
        DataSet dataSet = toDataSet(new RoutingProblem(name, depot, visits));
        assertThat(dataSet.getName()).isEqualTo(name);
        assertThat(toDomain(dataSet.getDepot())).isEqualTo(depot);
        assertThat(dataSet.getVisits()).hasSameSizeAs(visits);
        assertThat(toDomain(dataSet.getVisits().get(0))).isEqualTo(visit);

        // data set -> domain
        RoutingProblem routingProblem = toDomain(dataSet);
        assertThat(routingProblem.getName()).isEqualTo(name);
        assertThat(routingProblem.getDepot()).isEqualTo(depot);
        assertThat(routingProblem.getVisits()).containsExactly(visit);
    }
}
