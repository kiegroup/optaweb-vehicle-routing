/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.domain.location.RoadLocation;
import org.optaweb.vehiclerouting.domain.LatLng;
import org.optaweb.vehiclerouting.domain.Location;
import org.optaweb.vehiclerouting.service.route.RouteChangedEvent;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouteOptimizerImplTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private Solver<TspSolution> solver;
    @Mock
    private BestSolutionChangedEvent<TspSolution> bestSolutionChangedEvent;
    @Captor
    private ArgumentCaptor<RouteChangedEvent> routeChangedEventArgumentCaptor;
    @InjectMocks
    private RouteOptimizerImpl routeOptimizer;

    @Test
    public void should_listen_for_best_solution_events() {
        verify(solver).addEventListener(routeOptimizer);
    }

    @Test
    public void ignore_new_best_solutions_when_unprocessed_fact_changes() {
        // arrange
        when(bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()).thenReturn(false);

        // act
        routeOptimizer.bestSolutionChanged(bestSolutionChangedEvent);

        // assert
        verify(bestSolutionChangedEvent, never()).getNewBestSolution();
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    public void publish_new_best_solution_if_all_fact_changes_processed() {
        TspSolution solution = createSolutionWithOneVisit();
        Domicile domicile = solution.getDomicile();
        Visit visit = solution.getVisitList().get(0);
        when(bestSolutionChangedEvent.isEveryProblemFactChangeProcessed()).thenReturn(true);
        when(bestSolutionChangedEvent.getNewBestSolution()).thenReturn(solution);

        routeOptimizer.bestSolutionChanged(bestSolutionChangedEvent);

        verify(eventPublisher).publishEvent(routeChangedEventArgumentCaptor.capture());
        RouteChangedEvent event = routeChangedEventArgumentCaptor.getValue();

        Assertions.assertThat(event.getRoute()).containsExactly(
                new Location(
                        domicile.getLocation().getId(),
                        LatLng.valueOf(domicile.getLocation().getLatitude(), domicile.getLocation().getLongitude())
                ),
                new Location(
                        visit.getLocation().getId(),
                        LatLng.valueOf(visit.getLocation().getLatitude(), visit.getLocation().getLongitude())
                )
        );
    }

    @Test
    public void addLocation() {
    }

    @Test
    public void removeLocation() {
    }

    private static TspSolution createSolutionWithOneVisit() {
        TspSolution solution = new TspSolution();
        RoadLocation domicileLocation = new RoadLocation(1, 0.0, 1.0);
        RoadLocation visitLocation = new RoadLocation(2, 3.0, -1.0);
        solution.setLocationList(Arrays.asList(domicileLocation, visitLocation));
        Domicile domicile = new Domicile();
        domicile.setLocation(domicileLocation);
        solution.setDomicile(domicile);
        Visit visit = new Visit();
        visit.setLocation(visitLocation);
        visit.setPreviousStandstill(domicile);
        solution.setVisitList(Arrays.asList(visit));
        return solution;
    }
}
