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

import { UnpluggedIcon } from '@patternfly/react-icons';
import * as React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import ConnectionError from 'src/components/ConnectionError';
import TravelingSalesmanProblem, {
  ITravelingSalesmanProblemProps,
} from '../components/TravelingSalesmanProblem';
import { IAppState } from '../store/tsp';
import { ITSPRouteWithSegments, tspOperations } from '../store/tsp/index';
import * as types from '../store/tsp/types';
import './App.css';

export interface IAppProps extends ITravelingSalesmanProblemProps {
  tsp: ITSPRouteWithSegments & types.IWSConnection;
  removeHandler: (id: number) => void;
  loadHandler: () => void;
  addHandler: (e: React.SyntheticEvent<HTMLElement>) => void;
  clearHandler: () => void;
}

const mapStateToProps = ({ tsp }: IAppState) => ({ tsp });

const mapDispatchToProps = (dispatch: Dispatch) => ({
  loadHandler() {
    dispatch(tspOperations.loadDemo());
  },
  addHandler(e: any) {
    dispatch(tspOperations.addLocation(e.latlng));
  },
  clearHandler() {
    dispatch(tspOperations.clearSolution());
  },
  removeHandler(id: number) {
    dispatch(tspOperations.deleteLocation(id));
  },
});

class App extends React.Component<IAppProps> {
  constructor(props: IAppProps) {
    super(props);
    this.onClickRemove = this.onClickRemove.bind(this);
  }

  onClickRemove(id: number) {
    const {
      tsp: { domicileId, route },
      removeHandler,
    } = this.props;
    if (id !== domicileId || route.length === 1) {
      removeHandler(id);
    }
  }

  render() {
    const { ws } = this.props.tsp;
    return (
      <div>
        {ws === types.WebSocketConnectionState.ERROR && (
          <ConnectionError
            title="Oops... Connection error!"
            message="Please check your network connection."
            icon={<UnpluggedIcon />}
            help={
              'When connection is available the application will be functional again.'
            }
          />
        )}
        <TravelingSalesmanProblem {...this.props} />
      </div>
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(App);
