package org.optaweb.vehiclerouting.plugin.rest.model;

import static java.util.Arrays.asList;
import static org.optaweb.vehiclerouting.plugin.rest.model.PortableCoordinates.fromCoordinates;
import static org.optaweb.vehiclerouting.plugin.rest.model.PortableLocation.fromLocation;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.util.jackson.JacksonAssertions;
import org.optaweb.vehiclerouting.util.junit.FileContent;

class PortableRouteTest {

    @Test
    void marshal_to_json(@FileContent("portable-route.json") String expectedJson) {
        PortableVehicle vehicle = new PortableVehicle(13, "Vehicle", 45317);
        PortableLocation depot = visit(8, 42.6501218, -71.8835449, "Test depot");
        PortableLocation visit1 = visit(100, 42.7066596, -72.4934873, "Visit 1");
        PortableLocation visit2 = visit(200, 42.5543343, -71.4438280, "Visit 2");

        PortableRoute portableRoute = new PortableRoute(
                vehicle,
                depot,
                asList(visit1, visit2),
                asList(
                        asList(
                                coordinates(42.65005, -71.88522),
                                coordinates(42.64997, -71.88527)),
                        asList(
                                coordinates(42.64994, -71.88537),
                                coordinates(42.64994, -71.88542))));
        JacksonAssertions.assertThat(portableRoute).serializedIsEqualToJson(expectedJson);
    }

    private static PortableLocation visit(long id, double latitude, double longitude, String description) {
        return fromLocation(new Location(id, Coordinates.of(latitude, longitude), description));
    }

    private static PortableCoordinates coordinates(double latitude, double longitude) {
        return fromCoordinates(Coordinates.of(latitude, longitude));
    }
}
