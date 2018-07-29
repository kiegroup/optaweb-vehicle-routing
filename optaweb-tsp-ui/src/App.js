import { OrderedMap } from 'immutable';
import React, { Component } from 'react';
import 'tachyons/css/tachyons.css';
import LocationList from './LocationList';
import TspMap from './TspMap';

class App extends Component {
  constructor() {
    super();

    this.state = {
      center: {
        lat: 49.231782,
        lng: 16.575610,
      },
      zoom: 14,
      counter: -1,
      locations: OrderedMap(),
    };

    this.onClickMap = this.onClickMap.bind(this);
    this.onClickRemove = this.onClickRemove.bind(this);
  }

  onClickMap(e) {
    console.log(e.latlng);
    const id = this.state.counter + 1;
    this.setState({
      counter: id,
      locations: this.state.locations.set(id, e.latlng),
    });
    console.log(`Locations: ${this.state.locations}`);
  }

  onClickRemove(id) {
    console.log(`Removing location ${id}`);
    this.setState({
      locations: this.state.locations.remove(id),
    });
  }

  render() {
    const { center, zoom, locations } = this.state;
    console.log(`Render, center: ${center}, locations: [${locations}]`);

    return (
      <div>
        <LocationList
          locations={locations}
          removeHandler={this.onClickRemove}
        />
        <TspMap
          center={center}
          zoom={zoom}
          locations={locations}
          clickHandler={this.onClickMap}
          removeHandler={this.onClickRemove}
        />
      </div>
    );
  }
}

export default App;
