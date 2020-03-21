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

package org.optaweb.vehiclerouting.domain;

/**
 * Travel cost (distance between two {@link Location locations} or the length of a {@link Route route}).
 */
public class Distance {

    private final long seconds;

    public static Distance ofSeconds(long seconds) {
        return new Distance(seconds);
    }

    private Distance(long seconds) {
        this.seconds = seconds;
    }

    public long seconds() {
        return seconds;
    }

    @Override
    public String toString() {
        return seconds + " s";
    }
}
