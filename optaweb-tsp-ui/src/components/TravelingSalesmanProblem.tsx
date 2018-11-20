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
import LocationList from "./LocationList";
import TspMap from "./TspMap";
import { TSPRoute, LatLng } from "../store/tsp/types";

export interface TravelingSalesmanProblemProps {
  tsp: TSPRoute;
  removeHandler: (id: number) => void;
  loadHandler: () => void;
  addHandler:(e: React.SyntheticEvent<HTMLElement>) => void;
}

interface TravelingSalesmanProblemState {
  center: LatLng;
  zoom: number;
  selectedId: number
}

export default class TravelingSalesmanProblem extends React.Component<TravelingSalesmanProblemProps, TravelingSalesmanProblemState> {
  static defaultProps = {
    tsp: {
      route: [],
      domicileId: -1,
      distance: "0"
    }
  };
  constructor(props: TravelingSalesmanProblemProps) {
    super(props);

    this.state = {
      center: {
        lat: 49.23178,
        lng: 16.57561
      },
      zoom: 5,
      selectedId: NaN
    };
    this.onSelectLocation = this.onSelectLocation.bind(this);
  }

  onSelectLocation(id: number) {
    this.setState({ selectedId: id });
  }

  public render() {
    const { center, zoom, selectedId } = this.state;
    const {
      tsp: { route, domicileId, distance },
      removeHandler,
      loadHandler,
      addHandler
    } = this.props;
    console.log(
      `Render, center: ${center}, route: [${route}], selected: ${selectedId}`
    );

    return (
      <div>
        <LocationList
          route={route}
          domicileId={domicileId}
          distance={distance}
          removeHandler={removeHandler}
          selectHandler={this.onSelectLocation}
          loadHandler={loadHandler}
        />
        <TspMap
          center={center}
          zoom={zoom}
          selectedId={selectedId}
          route={route}
          domicileId={domicileId}
          clickHandler={addHandler}
          removeHandler={removeHandler}
        />
      </div>
    );
  }
}
