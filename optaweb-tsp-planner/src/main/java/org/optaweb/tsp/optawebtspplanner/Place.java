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

package org.optaweb.tsp.optawebtspplanner;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // https://wiki.openstreetmap.org/wiki/Node#Structure
    @Column(precision = 9, scale = 7)
    @JsonProperty(value = "lat", required = true)
    private BigDecimal latitude;
    @Column(precision = 10, scale = 7)
    @JsonProperty(value = "lng", required = true)
    private BigDecimal longitude;

    public Place() {
    }

    public Place(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
