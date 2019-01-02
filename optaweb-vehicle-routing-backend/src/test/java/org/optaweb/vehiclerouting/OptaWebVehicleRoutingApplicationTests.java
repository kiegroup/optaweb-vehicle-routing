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

package org.optaweb.vehiclerouting;

import java.math.BigDecimal;

import org.assertj.core.api.SoftAssertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.optaweb.vehiclerouting.plugin.persistence.LocationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(Profiles.TEST)
public class OptaWebVehicleRoutingApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void all_possible_values_should_be_persisted_without_loss_of_precision() {
        // arrange
        // https://wiki.openstreetmap.org/wiki/Node#Structure
        final BigDecimal maxLatitude = new BigDecimal("90.0000000");
        final BigDecimal maxLongitude = new BigDecimal("214.7483647");
        final BigDecimal minLatitude = maxLatitude.negate();
        final BigDecimal minLongitude = maxLongitude.negate();
        restTemplate.postForEntity("/locations", new LocationEntity(minLatitude, minLongitude), LocationEntity.class);
        restTemplate.postForEntity("/locations", new LocationEntity(maxLatitude, maxLongitude), LocationEntity.class);

        // act
        ResponseEntity<LocationEntity> response1 = restTemplate.getForEntity("/locations/1", LocationEntity.class);
        ResponseEntity<LocationEntity> response2 = restTemplate.getForEntity("/locations/2", LocationEntity.class);

        // assert
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response1.getBody().getLatitude()).isEqualTo(minLatitude);
            softly.assertThat(response1.getBody().getLongitude()).isEqualTo(minLongitude);
            softly.assertThat(response2.getBody().getLatitude()).isEqualTo(maxLatitude);
            softly.assertThat(response2.getBody().getLongitude()).isEqualTo(maxLongitude);
        });
    }

    @Test
    public void should_create_entity() throws Exception {
        mockMvc.perform(post("/locations").content(
                "{\"latitude\": \"1\", \"longitude\":\"2\"}")).andExpect(
                status().isCreated()).andExpect(
                header().string(HttpHeaders.LOCATION, containsString("locations/")));
    }

    @Test
    @Ignore
    public void values_close_to_zero_should_not_use_scientific_format() throws Exception {
        // arrange
        final String lat = "0.0000003";
        final String lng = "0.0000005";
        MvcResult mvcResult = mockMvc.perform(post("/locations").content(
                "{\"lat\": \"" + lat + "\", \"lng\":\"" + lng + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        // act & assert
        String location = mvcResult.getResponse().getHeader(HttpHeaders.LOCATION);
        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lat").value(lat))
                .andExpect(jsonPath("$.lng").value(lng));
    }
}
