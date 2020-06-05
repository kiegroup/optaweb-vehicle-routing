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

import {
  BaseSizes,
  Expandable,
  Flex,
  FlexItem,
  List,
  ListItem,
  Modal,
  Text,
  TextContent,
  TextVariants,
  Title,
  TitleLevel,
} from '@patternfly/react-core';
import { UnpluggedIcon } from '@patternfly/react-icons';
import { backendUrl } from 'common';
import * as React from 'react';

export interface ConnectionErrorProps {
  isOpen: boolean;
}

const title = 'Connection error';

const header = (
  <Flex>
    <FlexItem>
      <UnpluggedIcon size="md" />
    </FlexItem>
    <FlexItem>
      <Title headingLevel={TitleLevel.h1} size={BaseSizes['2xl']}>
        {title}
      </Title>
    </FlexItem>
  </Flex>
);

const ConnectionError: React.FC<ConnectionErrorProps> = ({ isOpen }) => (
  <Modal title={title} header={header} isOpen={isOpen} isSmall>
    <TextContent>
      <Text component={TextVariants.p}>
        Cannot connect to server.
      </Text>
      <Expandable toggleText="Show more">
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
      </Expandable>
    </TextContent>
  </Modal>
);

export default ConnectionError;
