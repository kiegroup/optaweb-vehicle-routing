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
      distance: '',
      stompClient: null,
    };

    this.onClickLoad = this.onClickLoad.bind(this);
    this.onClickMap = this.onClickMap.bind(this);
    this.onClickRemove = this.onClickRemove.bind(this);
    this.onSelectLocation = this.onSelectLocation.bind(this);
  }

  componentDidMount() {
    this.connect();
  }

  onClickLoad() {
    this.state.stompClient.send('/app/demo');
  }

  onClickMap(e) {
    console.log(e.latlng);
    this.state.stompClient.send('/app/place', JSON.stringify(e.latlng));
  }

  onClickRemove(id) {
    if (id !== this.state.domicileId || this.state.route.length === 1) {
      this.state.stompClient.send(`/app/place/${id}/delete`);
    }
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
        const tsp = JSON.parse(res.body);
        this.setState({
          route: tsp.route,
          domicileId: tsp.route.length > 0 ? tsp.route[0].id : NaN,
          distance: tsp.distance,
        });
      });
    });
  }

  render() {
    const { center, zoom, selectedId, route, domicileId, distance } = this.state;
    console.log(`Render, center: ${center}, route: [${route}], selected: ${selectedId}`);

    return (
      <div>
        <LocationList
          route={route}
          domicileId={domicileId}
          distance={distance}
          removeHandler={this.onClickRemove}
          selectHandler={this.onSelectLocation}
          loadHandler={this.onClickLoad}
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
