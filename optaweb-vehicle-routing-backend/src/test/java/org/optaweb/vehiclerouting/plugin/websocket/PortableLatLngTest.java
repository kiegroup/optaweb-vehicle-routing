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

package org.optaweb.vehiclerouting.plugin.websocket;

import java.math.BigDecimal;

import org.junit.Test;
import org.optaweb.vehiclerouting.domain.LatLng;

import static org.assertj.core.api.Assertions.assertThat;

public class PortableLatLngTest {

    @Test
    public void fromLatLng() {
        LatLng latLng = LatLng.valueOf(0.04687, -88.8889);
        PortableLatLng portableLatLng = PortableLatLng.fromLatLng(latLng);
        assertThat(portableLatLng.getLatitude()).isEqualTo(latLng.getLatitude());
        assertThat(portableLatLng.getLongitude()).isEqualTo(latLng.getLongitude());
    }

    @Test
    public void should_reduce_scale_if_needed() {
        LatLng latLng = LatLng.valueOf(0.123450001, -88.999999999);
        LatLng scaledDown = LatLng.valueOf(0.12345, -89);
        PortableLatLng portableLatLng = PortableLatLng.fromLatLng(latLng);
        assertThat(portableLatLng.getLatitude()).isEqualTo(scaledDown.getLatitude());
        assertThat(portableLatLng.getLongitude()).isEqualByComparingTo(scaledDown.getLongitude());
        // This would surprisingly fail because actual is -89 and expected is -89.0
//        assertThat(portableLatLng.getLongitude()).isEqualTo(scaledDown.getLongitude());
    }

    @Test
    public void equals_hashCode_toString() {
        BigDecimal lat1 = BigDecimal.valueOf(10.0101);
        BigDecimal lat2 = BigDecimal.valueOf(20.2323);
        BigDecimal lon1 = BigDecimal.valueOf(-8.7);
        BigDecimal lon2 = BigDecimal.valueOf(-7.8);
        PortableLatLng portableLatLng = new PortableLatLng(lat1, lon1);

        // equals()
        assertThat(portableLatLng).isNotEqualTo(null);
        assertThat(portableLatLng).isNotEqualTo(new LatLng(lat1, lon1));
        assertThat(portableLatLng).isNotEqualTo(new PortableLatLng(lat1, lon2));
        assertThat(portableLatLng).isNotEqualTo(new PortableLatLng(lat2, lon1));
        assertThat(portableLatLng).isEqualTo(portableLatLng);
        assertThat(portableLatLng).isEqualTo(new PortableLatLng(lat1, lon1));

        // hasCode()
        assertThat(portableLatLng).hasSameHashCodeAs(new PortableLatLng(lat1, lon1));

        // toString()
        assertThat(portableLatLng.toString()).contains(
                lat1.toPlainString(),
                lon1.toPlainString());
    }
}
