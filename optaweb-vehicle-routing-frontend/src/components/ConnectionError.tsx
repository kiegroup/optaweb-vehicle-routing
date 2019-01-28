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

import { Modal, Text, TextContent, TextVariants } from '@patternfly/react-core';
import * as React from 'react';

export interface IConnectionErrorProps {
  title: string;
  message: string;
  icon?: React.ReactNode;
  help?: string;
}

const renderHelpBlock = (help?: string) => {
  return help ? (
    <Text component={TextVariants.small}>{help}</Text>
  ) : (
    ''
  );
};

const ConnectionError: React.SFC<IConnectionErrorProps> = (
  { title, message, icon, help }: IConnectionErrorProps,
) => {
  return (
    <Modal title={title} isOpen={true}>
      <TextContent>
        <Text component={TextVariants.h3}>
          {icon}
          {message}
          {renderHelpBlock(help)}
        </Text>
      </TextContent>
    </Modal>
  );
};

export default ConnectionError;
