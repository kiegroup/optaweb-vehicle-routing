/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import { IAppState } from 'store/configStore';
import { websocketOperations } from 'store/websocket';
import { WebSocketConnectionStatus } from 'store/websocket/types';
import ConnectionError from 'ui/connection/ConnectionError';

interface IStateProps {
  connectionStatus: WebSocketConnectionStatus;
}

interface IDispatchProps {
  connectClient: typeof websocketOperations.connectClient;
}

type Props = IStateProps & IDispatchProps;

const mapStateToProps = ({ connectionStatus }: IAppState): IStateProps => ({
  connectionStatus,
});

const mapDispatchToProps: IDispatchProps = {
  connectClient: websocketOperations.connectClient,
};

class ConnectionManager extends React.Component<Props> {

  componentDidMount() {
    this.props.connectClient();
  }

  render() {
    return (
      <ConnectionError
        isOpen={this.props.connectionStatus === WebSocketConnectionStatus.ERROR}
        message={'Please check your network connection.'}
        title={'Oops... Connection error!'}
        icon={<UnpluggedIcon />}
        help={'When connection is available the application will be functional again.'}
      />
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(ConnectionManager);
