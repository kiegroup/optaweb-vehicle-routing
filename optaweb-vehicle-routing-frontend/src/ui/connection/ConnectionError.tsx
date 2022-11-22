import {
  ExpandableSection,
  List,
  ListItem,
  Modal,
  Text,
  TextContent,
  TextVariants,
} from '@patternfly/react-core';
import { backendUrl } from 'common';
import * as React from 'react';

export interface ConnectionErrorProps {
  isOpen: boolean;
}

const title = 'Connection error';

const ConnectionError: React.FC<ConnectionErrorProps> = ({ isOpen }) => (
  <Modal
    title={title}
    titleIconVariant="danger"
    isOpen={isOpen}
    variant="small"
    showClose={false}
    aria-label={title}
  >
    <TextContent>
      <Text component={TextVariants.p}>
        The server is unreachable. Trying to reconnect.
      </Text>
      <ExpandableSection toggleText="Show more">
        <Text component={TextVariants.p}>
          The server is expected to be running at
          {' '}
          <a href={backendUrl} target="_blank" rel="noopener noreferrer">{backendUrl}</a>
          {' '}
          but the connection failed.
        </Text>
        <Text component={TextVariants.p}>
          Please check the following possible reasons and try to resolve them:
        </Text>
        <List>
          <ListItem>You are offline. Check your network connection.</ListItem>
          <ListItem>The server is running on a different URL. Check if the URL is incorrect.</ListItem>
          <ListItem>The server is down. Restart the server.</ListItem>
        </List>
        <Text>
          The application will reconnect as soon as the server is available again.
        </Text>
      </ExpandableSection>
    </TextContent>
  </Modal>
);

export default ConnectionError;
