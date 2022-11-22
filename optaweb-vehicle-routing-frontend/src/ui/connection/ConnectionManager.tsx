import * as React from 'react';
import { connect } from 'react-redux';
import { AppState } from 'store/types';
import { websocketOperations } from 'store/websocket';
import { WebSocketConnectionStatus } from 'store/websocket/types';
import ConnectionError from 'ui/connection/ConnectionError';

interface StateProps {
  connectionStatus: WebSocketConnectionStatus;
}

interface DispatchProps {
  connectClient: typeof websocketOperations.connectClient;
}

export type Props = StateProps & DispatchProps;

const mapStateToProps = ({ connectionStatus }: AppState): StateProps => ({
  connectionStatus,
});

const mapDispatchToProps: DispatchProps = {
  connectClient: websocketOperations.connectClient,
};

export class ConnectionManager extends React.Component<Props> {
  componentDidMount() {
    this.props.connectClient();
  }

  render() {
    return (
      <ConnectionError isOpen={this.props.connectionStatus === WebSocketConnectionStatus.ERROR} />
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(ConnectionManager);
