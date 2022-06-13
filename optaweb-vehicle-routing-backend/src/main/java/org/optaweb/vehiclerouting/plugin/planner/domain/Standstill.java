package org.optaweb.vehiclerouting.plugin.planner.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

@PlanningEntity
public interface Standstill {

    /**
     * The standstill's location.
     *
     * @return never {@code null}
     */
    PlanningLocation getLocation();

    /**
     * The next visit after this standstill.
     *
     * @return sometimes {@code null}
     */
    @InverseRelationShadowVariable(sourceVariableName = "previousStandstill")
    PlanningVisit getNextVisit();

    void setNextVisit(PlanningVisit nextVisit);
}
