/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
            action={(
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
