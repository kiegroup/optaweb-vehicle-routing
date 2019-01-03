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
import org.optaweb.vehiclerouting.domain.Location;

import static org.assertj.core.api.Assertions.assertThat;

public class PortableLocationTest {

    @Test
    public void fromLocation() {
        Location location = new Location(17, LatLng.valueOf(5.1, -0.0007));
        PortableLocation portableLocation = PortableLocation.fromLocation(location);
        assertThat(portableLocation.getId()).isEqualTo(location.getId());
        assertThat(portableLocation.getLatitude()).isEqualTo(location.getLatLng().getLatitude());
        assertThat(portableLocation.getLongitude()).isEqualTo(location.getLatLng().getLongitude());
    }

    @Test
    public void fromLatLng() {
        LatLng latLng = LatLng.valueOf(0.04687, -88.8889);
        PortableLocation portableLocation = PortableLocation.fromLatLng(latLng);
        assertThat(portableLocation.getId()).isZero();
        assertThat(portableLocation.getLatitude()).isEqualTo(latLng.getLatitude());
        assertThat(portableLocation.getLongitude()).isEqualTo(latLng.getLongitude());
    }

    @Test
    public void equals_hashCode_toString() {
        long id = 123456;
        BigDecimal lat1 = BigDecimal.valueOf(10.0101);
        BigDecimal lat2 = BigDecimal.valueOf(20.2323);
        BigDecimal lon1 = BigDecimal.valueOf(-8.7);
        BigDecimal lon2 = BigDecimal.valueOf(-7.8);
        PortableLocation portableLocation = new PortableLocation(id, lat1, lon1);

        // equals()
        assertThat(portableLocation).isNotEqualTo(null);
        assertThat(portableLocation).isNotEqualTo(new Location(id, new LatLng(lat1, lon1)));
        assertThat(portableLocation).isNotEqualTo(new PortableLocation(id + 1, lat1, lon1));
        assertThat(portableLocation).isNotEqualTo(new PortableLocation(id, lat1, lon2));
        assertThat(portableLocation).isNotEqualTo(new PortableLocation(id, lat2, lon1));
        assertThat(portableLocation).isEqualTo(portableLocation);
        assertThat(portableLocation).isEqualTo(new PortableLocation(id, lat1, lon1));

        // hasCode()
        assertThat(portableLocation).hasSameHashCodeAs(new PortableLocation(id, lat1, lon1));

        // toString()
        assertThat(portableLocation.toString())
                .contains(String.valueOf(id))
                .contains(lat1.toPlainString())
                .contains(lon1.toPlainString());
    }
}
