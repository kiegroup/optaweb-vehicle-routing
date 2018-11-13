/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import Location from "./Location";
import { TSPRoute, GPSLocation } from "./store/tsp/types";

export interface LocationListProps extends TSPRoute {
  removeHandler: (id: number) => void;
  selectHandler: (e: any) => void;
  loadHandler: () => void;
}

const LocationList: React.SFC<LocationListProps> = ({
  route,
  domicileId,
  distance = "",
  removeHandler,
  selectHandler,
  loadHandler
}: LocationListProps) => {
  return (
    <div className="leaflet-top leaflet-left leaflet-touch">
      <div className="leaflet-control leaflet-bar w5 bg-white">
        {route.length === 0 ? (
          <div className="tc ma2">
            <div>Click map to add locations</div>
            <div>
              or
              <button
                type="button"
                style={{ width: "100%" }}
                onClick={loadHandler}
              >
                Load 40 European cities
              </button>
            </div>
          </div>
        ) : (
          <div>
            <div className="tl ma2 pa2">Distance: {distance}</div>
            <div className="tl ma2 pa2">Locations: {route.length}</div>
            {/*
               The calculated maxHeight is a hack because the constant 116px depends
               on the height of Distance and Locations rows (above) and individual location rows.
               */}
            <div
              style={{ maxHeight: "calc(100vh - 116px)", overflowY: "auto" }}
            >
              {route
                .slice(0) // clone the array because
                // sort is done in place (that would affect the route)
                .sort((a, b) => a.id - b.id)
                .map(location => (
                  <Location
                    key={location.id}
                    id={location.id}
                    removeDisabled={
                      route.length > 1 && location.id === domicileId
                    }
                    removeHandler={removeHandler}
                    selectHandler={selectHandler}
                  />
                ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default LocationList;
