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
import org.optaplanner.test.api.solver.change.MockProblemChangeDirector;

public class MockSolver<Solution_> {

    private final Solution_ workingSolution;
    private final MockProblemChangeDirector changeDirector;

    public static <Solution_> MockSolver<Solution_> build(Solution_ solution) {
        MockProblemChangeDirector spy = Mockito.spy(new MockProblemChangeDirector());
        return new MockSolver<>(solution, spy);
    }

    private MockSolver(Solution_ workingSolution, MockProblemChangeDirector changeDirector) {
        this.workingSolution = workingSolution;
        this.changeDirector = changeDirector;
    }

    // ************************************************************************
    // Problem change API from Solver.
    // ************************************************************************

    public void addProblemChange(ProblemChange<Solution_> problemChange) {
        problemChange.doChange(workingSolution, changeDirector);
    }

    // ************************************************************************
    // Lookup API from MockProblemChangeDirector.
    // ************************************************************************

    public MockProblemChangeDirector.LookUpMockBuilder whenLookingUp(Object forObject) {
        return changeDirector.whenLookingUp(forObject);
    }

    // ************************************************************************
    // Simplified verification API.
    // ************************************************************************

    public void verifyEntityAdded(Object entity) {
        verify(changeDirector).addEntity(same(entity), any());
    }

    public void verifyEntityRemoved(Object entity) {
        verify(changeDirector).removeEntity(same(entity), any());
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

    public void verifyProblemPropertyChanged(Object entityOrFact) {
        verify(changeDirector).changeProblemProperty(same(entityOrFact), any());
    }
}
