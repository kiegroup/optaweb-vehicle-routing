package org.optaweb.vehiclerouting.service.region;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Provides information about the working region.
 */
@ApplicationScoped
public class RegionService {

    private final RegionProperties regionProperties;
    private final Region region;

    @Inject
    RegionService(RegionProperties regionProperties, Region region) {
        this.regionProperties = regionProperties;
        this.region = region;
    }

    /**
     * Country codes matching the working region.
     *
     * @return country codes (never {@code null})
     */
    public List<String> countryCodes() {
        return regionProperties.countryCodes();
    }

    /**
     * Bounding box of the working region.
     *
     * @return bounding box of the working region.
     */
    public BoundingBox boundingBox() {
        return region.getBounds();
    }
}
