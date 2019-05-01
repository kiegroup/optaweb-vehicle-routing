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

import java.io.InputStreamReader;
import java.io.Reader;

import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.service.demo.dataset.DataSetMarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingProblemConfig {

    private final DataSetMarshaller dataSetMarshaller;

    @Autowired
    public RoutingProblemConfig(DataSetMarshaller dataSetMarshaller) {
        this.dataSetMarshaller = dataSetMarshaller;
    }

    @Bean
    public RoutingProblem routingProblem() {
        return dataSetMarshaller.unmarshall(belgiumReader());
    }

    private Reader belgiumReader() {
        return new InputStreamReader(DemoService.class.getResourceAsStream("belgium-cities.yaml"));
    }
}
