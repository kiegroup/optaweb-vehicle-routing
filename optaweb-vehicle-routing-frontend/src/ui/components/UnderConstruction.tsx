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

import { Bullseye, GutterSize, Stack, StackItem, Text, TextContent, TextVariants } from '@patternfly/react-core';
import { HammerIcon } from '@patternfly/react-icons';
import * as React from 'react';

export function UnderConstruction() {
  return (
    <Bullseye>
      <div>
        <Stack gutter={GutterSize.md}>
          <StackItem isFilled={false}>
            <Bullseye>
              <HammerIcon height={48} width={48} />
            </Bullseye>
          </StackItem>
          <StackItem isFilled={false}>
            <TextContent>
              <Text component={TextVariants.h1}>
                Under construction
              </Text>
            </TextContent>
          </StackItem>
        </Stack>
      </div>
    </Bullseye>
  );
}
