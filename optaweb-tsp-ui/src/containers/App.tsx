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
import { IAppState } from '../store/configStore';
import tspOperations from '../store/operations';
import { WebSocketConnectionStatus } from '../store/websocket/types';
import './App.css';

export interface IAppProps extends ITravelingSalesmanProblemProps {
  connectionStatus: WebSocketConnectionStatus;
}

const mapStateToProps = ({ route, connectionStatus }: IAppState): Partial<IAppProps> => ({
  connectionStatus,
  tsp: route,
});

const mapDispatchToProps = (dispatch: Dispatch): Partial<IAppProps> => ({
  addHandler: (e: any) => dispatch(tspOperations.addLocation(e.latlng)),
  clearHandler: () => dispatch(tspOperations.clearSolution()),
  loadHandler: () => dispatch(tspOperations.loadDemo()),
  removeHandler: (id: number) => dispatch(tspOperations.deleteLocation(id)),
});

class App extends React.Component<IAppProps> {
  constructor(props: IAppProps) {
    super(props);
  }

  render() {
    const { connectionStatus } = this.props;
    return (
      <div>
        {connectionStatus === WebSocketConnectionStatus.ERROR && (
          <ConnectionError
            title="Oops... Connection error!"
            message="Please check your network connection."
            icon={<UnpluggedIcon />}
            help="When connection is available the application will be functional again."
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
