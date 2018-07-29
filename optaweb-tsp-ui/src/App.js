import { OrderedMap } from 'immutable';
import React, { Component } from 'react';
import { Map, Marker, Popup, TileLayer, ZoomControl } from 'react-leaflet';
import 'tachyons/css/tachyons.css';

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

    this.onClickMap = this.onClickMap.bind(this);
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
    const { position, zoom, locations } = this.state;
    console.log(`Render, position: ${position}, locations: [${locations}]`);

    return (
      <div>
        <div className={'leaflet-top leaflet-left leaflet-touch'}>
          <div className={'leaflet-control leaflet-bar w5 bg-white '}>
            {
              locations.isEmpty() ?
                <div className={'tc ma2'}>Click map to add locations</div> :
                locations.keySeq().map(id => (
                  <div
                    key={id}
                    className={'ma2 flex'}
                  >
                    <span className={'w-80 pa2'}>{`Location ${id}`}</span>
                    <button className={'w-20 pa2'} onClick={() => this.onClickRemove(id)}>x
                    </button>
                  </div>
                ))
            }
          </div>
        </div>
        <Map
          center={position}
          zoom={zoom}
          onClick={this.onClickMap}
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
