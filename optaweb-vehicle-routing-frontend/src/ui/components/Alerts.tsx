import { Alert, AlertActionCloseButton, AlertGroup } from '@patternfly/react-core';
import * as React from 'react';
import { connect } from 'react-redux';
import { messageActions, messageSelectors } from 'store/message';
import { Message } from 'store/message/types';
import { AppState } from 'store/types';

interface StateProps {
  messages: Message[];
}

const mapStateToProps = ({ messages }: AppState): StateProps => ({
  messages: messageSelectors.getNewMessages(messages),
});

interface DispatchProps {
  readMessage: typeof messageActions.readMessage;
}

const mapDispatchToProps: DispatchProps = {
  readMessage: messageActions.readMessage,
};

export type Props = StateProps & DispatchProps;

export const Alerts: React.FC<Props> = ({ messages, readMessage }: Props) => (
  messages.length > 0 ? (
    <AlertGroup isToast>
      {messages
        .map(({ id, text }) => (
          <Alert
            key={id}
            variant="danger"
            title="Error"
            isLiveRegion
            actionClose={(
              <AlertActionCloseButton
                title="Close alert"
                onClose={() => readMessage(id)}
              />
            )}
          >
            {text}
          </Alert>
        ))}
    </AlertGroup>
  ) : null
);

export default connect(mapStateToProps, mapDispatchToProps)(Alerts);
