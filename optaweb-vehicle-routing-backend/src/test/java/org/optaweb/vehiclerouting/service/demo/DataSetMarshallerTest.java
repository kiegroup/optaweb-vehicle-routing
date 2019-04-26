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

package org.optaweb.vehiclerouting.service.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.optaweb.vehiclerouting.service.demo.dataset.DataSet;
import org.optaweb.vehiclerouting.service.demo.dataset.Location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataSetMarshallerTest {

    @Test
    public void unmarshall_data_set() throws IOException {
        DataSet dataSet;
        try (InputStream inputStream = DataSetMarshallerTest.class.getResourceAsStream("test-belgium.yaml")) {
            dataSet = new DataSetMarshaller().unmarshall(new InputStreamReader(inputStream));
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
        Location depot = new Location("Depot", -1.1, -9.9);
        Location location1 = new Location("Location 1", 1.0, 0.1);
        Location location2 = new Location("Location 2", 2.0, 0.2);
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
                .isThrownBy(() -> new DataSetMarshaller(objectMapper).unmarshall(mock(Reader.class)))
                .withRootCauseExactlyInstanceOf(IOException.class);

        when(objectMapper.writeValueAsString(any(DataSet.class))).thenThrow(JsonProcessingException.class);
        assertThatIllegalStateException()
                .isThrownBy(() -> new DataSetMarshaller(objectMapper).marshall(new DataSet()))
                .withRootCauseExactlyInstanceOf(JsonProcessingException.class);
    }
}
