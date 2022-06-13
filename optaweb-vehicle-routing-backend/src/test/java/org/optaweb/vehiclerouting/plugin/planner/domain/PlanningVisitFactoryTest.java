package org.optaweb.vehiclerouting.plugin.planner.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PlanningVisitFactoryTest {

    @Test
    void visit_should_have_same_id_as_location_and_default_demand() {
        long id = 4;
        PlanningLocation location = PlanningLocationFactory.testLocation(id);

        PlanningVisit visit = PlanningVisitFactory.fromLocation(location);

        assertThat(visit.getId()).isEqualTo(location.getId());
        assertThat(visit.getLocation()).isEqualTo(location);
        assertThat(visit.getDemand()).isEqualTo(PlanningVisitFactory.DEFAULT_VISIT_DEMAND);
    }
}
