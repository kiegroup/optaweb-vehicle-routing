package org.optaweb.vehiclerouting.plugin.planner;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class SolverTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        HashMap<String, String> config = new HashMap<>();
        config.put("quarkus.optaplanner.solver.termination.best-score-limit", "-1hard/-120soft");
        return config;
    }
}
