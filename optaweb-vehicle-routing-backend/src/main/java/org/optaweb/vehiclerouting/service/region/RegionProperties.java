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

import java.util.Collections;
import java.util.List;

import org.optaweb.vehiclerouting.domain.CountryCodeValidator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.region")
class RegionProperties {

    /**
     * List of ISO 3166-1 alpha-2 country code(s) matching the loaded OSM file.
     */
    private List<String> countryCodes = Collections.emptyList();

    /**
     * Get country codes matching the loaded OSM file (working region).
     * @return list of country codes (never {@code null})
     */
    public List<String> getCountryCodes() {
        return Collections.unmodifiableList(countryCodes);
    }

    public void setCountryCodes(List<String> countryCodes) {
        this.countryCodes = CountryCodeValidator.validate(countryCodes);
    }
}
