package org.optaweb.vehiclerouting.service.region;

import java.util.List;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "app.region")
interface RegionProperties {

    /**
     * Get country codes specified for the loaded OSM file (working region).
     * The codes are expected to be in the ISO 3166-1 alpha-2 format.
     *
     * @return list of country codes (never {@code null})
     */
    List<String> countryCodes();
}
