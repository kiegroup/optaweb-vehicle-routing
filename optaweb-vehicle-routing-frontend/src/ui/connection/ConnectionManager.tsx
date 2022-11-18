import { Component, ReactNode } from 'react';
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

export class ConnectionManager extends Component<Props> {
  componentDidMount(): void {
    this.props.connectClient();
  }

  render(): ReactNode {
    return (
      <ConnectionError isOpen={this.props.connectionStatus === WebSocketConnectionStatus.ERROR} />
    );
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(ConnectionManager);
