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
