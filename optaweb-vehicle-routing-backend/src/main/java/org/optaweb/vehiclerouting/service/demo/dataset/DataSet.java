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

package org.optaweb.vehiclerouting.service.demo.dataset;

import java.util.ArrayList;
import java.util.List;

/**
 * Data set representation used for marshalling and unmarshalling.
 */
class DataSet {

    private String name;
    private DataSetLocation depot;
    private List<DataSetLocation> visits = new ArrayList<>();

    /**
     * Data set name (a short description).
     * @return data set name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The depot.
     * @return the depot
     */
    public DataSetLocation getDepot() {
        return depot;
    }

    public void setDepot(DataSetLocation depot) {
        this.depot = depot;
    }

    /**
     * Visits.
     * @return visits
     */
    public List<DataSetLocation> getVisits() {
        return visits;
    }

    public void setVisits(List<DataSetLocation> visits) {
        this.visits = visits;
    }
}
