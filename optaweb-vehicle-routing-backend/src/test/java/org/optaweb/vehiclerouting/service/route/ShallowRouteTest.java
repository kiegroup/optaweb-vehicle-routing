package org.optaweb.vehiclerouting.service.route;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class ShallowRouteTest {

    @Test
    void shallow_route_to_string() {
        ShallowRoute shallowRoute = new ShallowRoute(200L, 100L, Arrays.asList(93L, 92L, 91L));
        assertThat(shallowRoute.toString()).containsSubsequence("200", "100", "93", "92", "91");
    }
}
