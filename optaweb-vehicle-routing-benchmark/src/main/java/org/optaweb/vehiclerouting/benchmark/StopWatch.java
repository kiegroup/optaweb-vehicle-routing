/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaweb.vehiclerouting.benchmark;

import java.util.ArrayList;
import java.util.List;

class StopWatch {

    private final long startTime;
    private long lastLap;
    private int lapCount = 0;
    private final List<Lap> laps = new ArrayList<>(1010);

    private StopWatch(long startTime) {
        this.startTime = startTime;
        lastLap = startTime;
    }

    static StopWatch start() {
        return new StopWatch(System.currentTimeMillis());
    }

    void lap() {
        long now = System.currentTimeMillis();
        Lap lap = new Lap(++lapCount, now - lastLap, now - startTime);
        laps.add(lap);
        lastLap = now;
    }

    void print() {
        laps.stream()
                .map(lap -> String.format("%4d%10d ms%10d s", lap.lapNumber, lap.lapTime, lap.totalTime / 1000))
                .forEach(System.out::println);
    }

    static class Lap {

        private final int lapNumber;
        private final long lapTime;
        private final long totalTime;

        Lap(int lapNumber, long lapTime, long totalTime) {
            this.lapNumber = lapNumber;
            this.lapTime = lapTime;
            this.totalTime = totalTime;
        }
    }
}
