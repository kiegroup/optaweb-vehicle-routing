package org.optaweb.vehiclerouting.domain;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class RouteWithTrackTest {

    private final Vehicle vehicle = VehicleFactory.testVehicle(4);
    private final Location depot = new Location(1, Coordinates.of(5, 5));
    private final Location visit1 = new Location(2, Coordinates.of(5, 5));
    private final Location visit2 = new Location(3, Coordinates.of(5, 5));

    @Test
    void constructor_args_not_null() {
        Route route = new Route(vehicle, depot, emptyList());
        assertThatNullPointerException().isThrownBy(() -> new RouteWithTrack(route, null));
        assertThatNullPointerException().isThrownBy(() -> new RouteWithTrack(null, emptyList()));
    }

    @Test
    void cannot_modify_track_externally() {
        Route route = new Route(vehicle, depot, Arrays.asList(visit1, visit2));
        ArrayList<List<Coordinates>> track = new ArrayList<>();
        track.add(Arrays.asList(Coordinates.of(1.0, 2.0)));

        List<List<Coordinates>> routeTrack = new RouteWithTrack(route, track).track();
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(routeTrack::clear);
    }

    @Test
    void when_route_is_empty_track_must_be_empty() {
        Route emptyRoute = new Route(vehicle, depot, emptyList());
        ArrayList<List<Coordinates>> track = new ArrayList<>();
        track.add(Arrays.asList(Coordinates.of(1.0, 2.0)));

        assertThatIllegalArgumentException().isThrownBy(() -> new RouteWithTrack(emptyRoute, track));
    }

    @Test
    void when_route_is_nonempty_track_must_be_nonempty() {
        Route route = new Route(vehicle, depot, Arrays.asList(visit1, visit2));
        ArrayList<List<Coordinates>> emptyTrack = new ArrayList<>();

        assertThatIllegalArgumentException().isThrownBy(() -> new RouteWithTrack(route, emptyTrack));
    }
}
