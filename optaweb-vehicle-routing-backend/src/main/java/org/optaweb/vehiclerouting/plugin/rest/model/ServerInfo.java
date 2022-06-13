package org.optaweb.vehiclerouting.plugin.rest.model;

import java.util.List;

/**
 * Server info suitable for network transport.
 */
public class ServerInfo {

    private final List<PortableCoordinates> boundingBox;
    private final List<String> countryCodes;
    private final List<RoutingProblemInfo> demos;

    public ServerInfo(List<PortableCoordinates> boundingBox, List<String> countryCodes, List<RoutingProblemInfo> demos) {
        this.boundingBox = boundingBox;
        this.countryCodes = countryCodes;
        this.demos = demos;
    }

    public List<PortableCoordinates> getBoundingBox() {
        return boundingBox;
    }

    public List<String> getCountryCodes() {
        return countryCodes;
    }

    public List<RoutingProblemInfo> getDemos() {
        return demos;
    }
}
