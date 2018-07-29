import { OrderedMap } from 'immutable';
import React, { Component } from 'react';
import { Map, Marker, Popup, TileLayer, ZoomControl } from 'react-leaflet';

class App extends Component {
  constructor() {
    super();

    this.state = {
      position: {
        lat: 49.231782,
        lng: 16.575610,
      },
      zoom: 14,
      counter: -1,
      locations: OrderedMap(),
    };

    this.handleClick = this.handleClick.bind(this);
  }

  handleClick(e) {
    console.log(e.latlng);
    const id = this.state.counter + 1;
    this.setState({
      counter: id,
      locations: this.state.locations.set(id, e.latlng),
    });
    console.log(`Locations: ${this.state.locations}`);
  }

  render() {
    const { position, zoom, locations } = this.state;
    console.log(`Render, position: ${position}, locations: [${locations}]`);

    return (
      <div>
        <div className={'leaflet-top leaflet-left leaflet-touch '}>
          <div
            className={'leaflet-control leaflet-bar'}
            style={{ backgroundColor: 'white' }}
          >
            {
              locations.keySeq().map(id => (
                <div key={id}>{`Location ${id}`}</div>
              ))
            }
          </div>
        </div>
        <Map
          center={position}
          zoom={zoom}
          onClick={this.handleClick}
          style={{ width: '100vw', height: '100vh' }}
          zoomControl={false} // hide the default zoom control which is on top left
        >
          <TileLayer
            attribution="&amp;copy <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <ZoomControl position="topright" />
          {
            locations.entrySeq().map(([id, location]) => (
              <Marker
                key={id}
                position={location}
              >
                <Popup>{`${id}: ${location.toString()}`}</Popup>
              </Marker>
            ))
          }
        </Map>
      </div>
    );
  }
}

export default App;
