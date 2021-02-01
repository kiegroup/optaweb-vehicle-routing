/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.service.location;

import org.optaweb.vehiclerouting.domain.Distance;

/**
 * Contains {@link Distance distances} from the location associated with this row to other locations.
 */
public interface DistanceMatrixRow {

    /**
     * Distance from this row's location to the given location.
     *
     * @param locationId target location
     * @return time it takes to travel to the given location
     */
    Distance distanceTo(long locationId);
}
