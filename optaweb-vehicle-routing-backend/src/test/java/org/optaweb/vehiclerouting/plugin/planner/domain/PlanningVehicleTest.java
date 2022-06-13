package org.optaweb.vehiclerouting.plugin.planner.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

class PlanningVehicleTest {

    @Test
    void get_future_visits_should_return_an_iterable_that_iterates_over_all_visits() {
        PlanningVisit visit1 = new PlanningVisit();
        PlanningVisit visit2 = new PlanningVisit();
        PlanningVisit visit3 = new PlanningVisit();

        PlanningVehicle vehicle = new PlanningVehicle();

        vehicle.setNextVisit(visit1);
        visit1.setNextVisit(visit2);
        visit2.setNextVisit(visit3);

        Iterable<PlanningVisit> futureVisits = vehicle.getFutureVisits();

        assertThat(futureVisits).containsExactly(visit1, visit2, visit3);
    }

    @Test
    void get_future_visits_should_throw_a_NoSuchElementException_when_there_are_no_more_visits() {
        PlanningVehicle vehicle = new PlanningVehicle();

        Iterator<PlanningVisit> futureVisits = vehicle.getFutureVisits().iterator();

        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(futureVisits::next);

        PlanningVisit visit1 = new PlanningVisit();
        PlanningVisit visit2 = new PlanningVisit();

        vehicle.setNextVisit(visit1);
        visit1.setNextVisit(visit2);

        futureVisits = vehicle.getFutureVisits().iterator();
        futureVisits.next();
        futureVisits.next();

        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(futureVisits::next);
    }
}
