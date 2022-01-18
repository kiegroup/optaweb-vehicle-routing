/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.plugin.planner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;

import org.mockito.Mockito;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaplanner.test.api.solver.change.MockProblemChangeDirector;

public class MockSolver<Solution_> {

    private final Solution_ workingSolution;
    private final ProblemChangeDirector changeDirector;

    public MockSolver(Solution_ workingSolution, ProblemChangeDirector changeDirector) {
        this.workingSolution = workingSolution;
        this.changeDirector = changeDirector;
    }

    public void addProblemChange(ProblemChange<Solution_> problemChange) {
        problemChange.doChange(workingSolution, changeDirector);
    }

    public static <Solution_> MockSolver<Solution_> build(Solution_ solution) {
        MockProblemChangeDirector spy = Mockito.spy(new MockProblemChangeDirector());
        return new MockSolver<>(solution, spy);
    }

    public void verifyVariableChanged(Object entity, String variableName) {
        verify(changeDirector).changeVariable(same(entity), eq(variableName), any());
    }

    public void verifyProblemFactAdded(Object fact) {
        verify(changeDirector).addProblemFact(same(fact), any());
    }

    public void verifyProblemFactRemoved(Object fact) {
        verify(changeDirector).removeProblemFact(same(fact), any());
    }

    public void verifyEntityAdded(Object entity) {
        verify(changeDirector).addEntity(same(entity), any());
    }
}
