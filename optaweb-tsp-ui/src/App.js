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
        lat: 49.23178,
        lng: 16.57561,
      },
      zoom: 5,
      counter: -1,
      selectedId: NaN,
      route: [],
      domicileId: NaN,
      stompClient: null,
    };

    this.onClickMap = this.onClickMap.bind(this);
    this.onClickRemove = this.onClickRemove.bind(this);
    this.onSelectLocation = this.onSelectLocation.bind(this);
  }

  componentDidMount() {
    this.connect();
  }

  onClickMap(e) {
    console.log(e.latlng);
    this.state.stompClient.send('/app/place', JSON.stringify(e.latlng));
  }

  onClickRemove(id) {
    this.state.stompClient.send(`/app/place/${id}/delete`);
  }

  onSelectLocation(id) {
    this.setState({ selectedId: id });
  }

  connect() {
    const socket = new SockJS('http://localhost:8080/tsp-websocket');
    const stompClient = webstomp.over(socket);
    this.setState({ stompClient });
    stompClient.connect({}, (frame) => {
      console.info('Connected:', frame);
      stompClient.subscribe('/topic/route', (res) => {
        const route = JSON.parse(res.body);
        this.setState({ route, domicileId: route.length > 0 ? route[0].id : NaN });
      });
    });
  }

  render() {
    const { center, zoom, selectedId, route, domicileId } = this.state;
    console.log(`Render, center: ${center}, route: [${route}], selected: ${selectedId}`);

    return (
      <div>
        <LocationList
          route={route}
          domicileId={domicileId}
          removeHandler={this.onClickRemove}
          selectHandler={this.onSelectLocation}
        />
        <TspMap
          center={center}
          zoom={zoom}
          selectedId={selectedId}
          route={route}
          domicileId={domicileId}
          clickHandler={this.onClickMap}
          removeHandler={this.onClickRemove}
        />
      </div>
    );
  }
}

export default App;
