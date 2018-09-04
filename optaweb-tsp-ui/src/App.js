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
      locations: [],
      selectedId: '',
    };

    this.onClickMap = this.onClickMap.bind(this);
    this.onClickRemove = this.onClickRemove.bind(this);
    this.onSelectLocation = this.onSelectLocation.bind(this);
  }

  componentDidMount() {
    fetch('http://localhost:8080/places/', {
      mode: 'cors',
      headers: {
        'Content-Type': 'application/json',
      },
    })
      .then(response => response.json())
      .then(places => this.setState({ locations: places._embedded.places }));
  }

  onClickMap(e) {
    console.log(e.latlng);
    fetch('http://localhost:8080/places/', {
      method: 'POST',
      mode: 'cors',
      body: JSON.stringify(e.latlng),
      headers: {
        'Content-Type': 'application/json',
      },
    }).then((res) => {
      if (res.ok) {
        return res.json();
      }
      console.error('Error:', res.statusText);
      throw new Error('Network response was not ok.');
    })
      .catch(error => console.error('Error:', error))
      .then(response => console.log('Success:', response));

    const id = this.state.counter + 1;
    this.setState({
      counter: id,
      locations: this.state.locations.set(id, e.latlng),
    });
  }

  onClickRemove(id) {
    this.setState({
      locations: this.state.locations.remove(id),
    });
  }

  onSelectLocation(id) {
    this.setState({ selectedId: id });
  }

  render() {
    const { center, zoom, locations, selectedId } = this.state;
    console.log(`Render, center: ${center}, locations: [${locations}], selected: ${selectedId}`);

    return (
      <div>
        <LocationList
          locations={locations}
          removeHandler={this.onClickRemove}
          selectHandler={this.onSelectLocation}
        />
        <TspMap
          center={center}
          zoom={zoom}
          locations={locations}
          selectedId={selectedId}
          clickHandler={this.onClickMap}
          removeHandler={this.onClickRemove}
        />
      </div>
    );
  }
}

export default App;
