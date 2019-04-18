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

import java.util.List;

/**
 * Server info suitable for network transport.
 */
public class ServerInfo {

    private final List<PortableLocation> boundingBox;
    private final List<String> countryCodes;

    public ServerInfo(List<PortableLocation> boundingBox, List<String> countryCodes) {
        this.boundingBox = boundingBox;
        this.countryCodes = countryCodes;
    }

    public List<PortableLocation> getBoundingBox() {
        return boundingBox;
    }

    public List<String> getCountryCodes() {
        return countryCodes;
    }
}
