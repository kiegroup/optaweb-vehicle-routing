import React, { Component } from 'react';
import SockJS from 'sockjs-client';
import 'tachyons/css/tachyons.css';
import webstomp from 'webstomp-client';
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
      route: [],
    };

    this.onClickMap = this.onClickMap.bind(this);
    this.onClickRemove = this.onClickRemove.bind(this);
    this.onSelectLocation = this.onSelectLocation.bind(this);
  }

  componentDidMount() {
    this.connect();
    this.loadLocations();
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
      .then((response) => {
        console.log('Success:', response);
        this.loadLocations();
      });
  }

  onClickRemove(id) {
    fetch(id, {
      method: 'DELETE',
      mode: 'cors',
    }).then((res) => {
      if (!res.ok) {
        console.error('Error:', res.statusText);
        throw new Error('Network response was not ok.');
      }
    }).catch(error => console.error('Error:', error))
      .then((response) => {
        console.log('Success:', response);
        this.loadLocations();
      });
  }

  onSelectLocation(id) {
    this.setState({ selectedId: id });
  }

  connect() {
    const socket = new SockJS('http://localhost:8080/tsp-websocket');
    const stompClient = webstomp.over(socket);
    stompClient.connect({}, (frame) => {
      console.info('Connected:', frame);
      stompClient.subscribe('/topic/route', (res) => {
        const route = JSON.parse(res.body);
        this.setState({
          route: route.map(place => [place.lat, place.lng]),
        });
      });
    });
  }

  loadLocations() {
    fetch('http://localhost:8080/places/', {
      mode: 'cors',
      headers: {
        'Content-Type': 'application/json',
      },
    }).then(response => response.json())
      .then(places => this.setState({ locations: places._embedded.places }));
  }

  render() {
    const { center, zoom, locations, selectedId, route } = this.state;
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
          route={route}
          clickHandler={this.onClickMap}
          removeHandler={this.onClickRemove}
        />
      </div>
    );
  }
}

export default App;
