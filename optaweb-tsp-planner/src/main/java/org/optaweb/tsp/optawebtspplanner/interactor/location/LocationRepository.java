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

package org.optaweb.tsp.optawebtspplanner.interactor.location;

import org.optaweb.tsp.optawebtspplanner.domain.LatLng;
import org.optaweb.tsp.optawebtspplanner.domain.Location;

/**
 * Defines repository operations on locations.
 */
public interface LocationRepository {

    /**
     * Create a location with a unique ID.
     * @param latLng location's coordinates
     * @return a new location
     */
    Location createLocation(LatLng latLng);

    /**
     * Remove location.
     * @param id location's id
     * @return the removed location
     */
    Location removeLocation(long id);
}
