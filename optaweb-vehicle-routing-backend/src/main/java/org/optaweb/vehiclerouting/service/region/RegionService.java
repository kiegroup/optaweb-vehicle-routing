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

package org.optaweb.vehiclerouting.service.region;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides information about the working region.
 */
@Service
public class RegionService {

    private final RegionProperties regionProperties;
    private final Region region;

    @Autowired
    RegionService(RegionProperties regionProperties, Region region) {
        this.regionProperties = regionProperties;
        this.region = region;
    }

    /**
     * Country codes matching the working region.
     * @return country codes (never null)
     */
    public List<String> countryCodes() {
        return regionProperties.getCountryCodes();
    }

    /**
     * Bounding box of the working region.
     * @return bounding box of the working region.
     */
    public BoundingBox boundingBox() {
        return region.getBounds();
    }
}
