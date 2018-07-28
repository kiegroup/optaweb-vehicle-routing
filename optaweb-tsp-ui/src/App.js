import React, { Component } from 'react';
import { Map, Marker, Popup, TileLayer } from 'react-leaflet';

class App extends Component {
  constructor() {
    super();

    this.state = {
      position: {
        lat: 51.505,
        lng: -0.09,
      },
      zoom: 13,
      locations: [],
    };

    this.handleClick = this.handleClick.bind(this);
  }

  handleClick(e) {
    console.log(e.latlng);
    this.setState({
      locations: [...this.state.locations, e.latlng],
    });
    console.log(`Locations: ${this.state.locations}`);
  }

  render() {
    const { position, zoom, locations } = this.state;
    console.log(`Render, position: ${position}, locations: [${locations}]`);

    return (
      <div>
        <div className={'leaflet-top leaflet-right leaflet-touch '}>
          <div
            className={'leaflet-control leaflet-bar'}
            style={{ backgroundColor: 'white' }}
          >
            {
              locations.map((location, index) => (
                <div key={location.toString()}>{`Location ${index}: ${location}`}</div>
              ))
            }
          </div>
        </div>
        <Map
          center={position}
          zoom={zoom}
          onClick={this.handleClick}
          style={{ width: '100vw', height: '100vh' }}
        >
          <TileLayer
            attribution="&amp;copy <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          {
            locations.map((location, index) => (
              <Marker
                key={location.toString()}
                position={location}
              >
                <Popup>{`${index}: ${location.toString()}`}</Popup>
              </Marker>
            ))
          }
        </Map>
      </div>
    );
  }
}

export default App;
