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
}
