package org.optaweb.vehiclerouting.service.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.optaweb.vehiclerouting.domain.Coordinates;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.domain.RoutingProblem;
import org.optaweb.vehiclerouting.domain.VehicleData;

class RoutingProblemListTest {

    @Test
    void should_validate_constructor_arguments() {
        assertThatNullPointerException().isThrownBy(() -> new RoutingProblemList(null));
    }

    @Test
    void should_fail_on_duplicate_problem_names() {
        String name = "DUPLICATE_NAME";
        RoutingProblem p1 = new RoutingProblem(name, Collections.emptyList(), null, Collections.emptyList());
        RoutingProblem p2 = new RoutingProblem(name, Collections.emptyList(), null, Collections.emptyList());
        assertThatIllegalStateException()
                .isThrownBy(() -> new RoutingProblemList(Stream.of(p1, p2)))
                .withMessageContaining(name);
    }

    @Test
    void all_by_name_should_return_expected_problems() {
        List<VehicleData> vehicles = Collections.emptyList();
        Location depot = new Location(0, Coordinates.of(10, -20));
        List<Location> visits = Collections.emptyList();
        String name1 = "Problem A";
        String name2 = "Problem B";
        RoutingProblemList routingProblemList = new RoutingProblemList(Stream.of(
                new RoutingProblem(name1, vehicles, depot, visits),
                new RoutingProblem(name2, vehicles, depot, visits)));

        assertThat(routingProblemList.all()).extracting("name").containsExactlyInAnyOrder(name1, name2);

        assertThat(routingProblemList.byName(name1).name()).isEqualTo(name1);

        assertThatIllegalArgumentException().isThrownBy(() -> routingProblemList.byName("Unknown problem"));
    }
}
