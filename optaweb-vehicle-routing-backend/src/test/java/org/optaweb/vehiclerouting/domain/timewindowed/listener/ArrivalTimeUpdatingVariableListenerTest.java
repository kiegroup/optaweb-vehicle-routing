/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.domain.timewindowed.listener;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Customer;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ArrivalTimeUpdatingVariableListenerTest {

    private Location location1 = new Location(1, Coordinates.valueOf(2.0, 2.0));
    private Location location2 = new Location(1, Coordinates.valueOf(1.0, 1.0));

    @Mock
    ScoreDirector scoreDirector;

    @Spy
    ArrivalTimeUpdatingVariableListener listener;

    @BeforeEach
    void setup(){
        listener = new ArrivalTimeUpdatingVariableListener();
    }

    @Test
    void afterVariableChanged_updates_scoreDirector() {
        listener.afterVariableChanged(scoreDirector, timeWindowedCustomer());
        verify(scoreDirector, times(1)).beforeVariableChanged(any(Customer.class), eq("arrivalTime"));
        verify(scoreDirector, times(1)).afterVariableChanged(any(Customer.class), eq("arrivalTime"));
    }

    @Test
    void afterEntityAdded_updates_scoreDirector() {
        listener.afterEntityAdded(scoreDirector, timeWindowedCustomer());
        verify(scoreDirector, times(1)).beforeVariableChanged(any(Customer.class), eq("arrivalTime"));
        verify(scoreDirector, times(1)).afterVariableChanged(any(Customer.class), eq("arrivalTime"));
    }

    ArrivalTimeUpdatingVariableListenerTest(){
        Map<Location, Double> travelMap1 = new HashMap<>();
        travelMap1.put(location2, 2000.0);
        location1.setTravelDistanceMap(travelMap1);
        Map<Location, Double> travelMap2 = new HashMap<>();
        travelMap2.put(location1, 2000.0);
        location2.setTravelDistanceMap(travelMap2);
    }

    private Customer timeWindowedCustomer(){
        TimeWindowedCustomer customer = new TimeWindowedCustomer();
        customer.setArrivalTime(2L);
        customer.setServiceDuration(2L);
        customer.setReadyTime(2L);
        customer.setLocation(location1);

        TimeWindowedCustomer previousCustomer = new TimeWindowedCustomer();
        previousCustomer.setArrivalTime(1L);
        previousCustomer.setReadyTime(1L);
        previousCustomer.setServiceDuration(0L);
        previousCustomer.setLocation(location2);

        customer.setPreviousStandstill(previousCustomer);
        return customer;
    }
}
