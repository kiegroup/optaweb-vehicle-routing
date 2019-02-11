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

import {
  FormGroup,
  PageSection,
  Text,
  TextContent,
  TextInput,
  TextVariants,
} from '@patternfly/react-core';
import { Table, TableBody, TableHeader } from '@patternfly/react-table';
import * as React from 'react';

const rows = [['Model Name', 'Capacity', 'Roads', '']];
const columns = ['Model Name', 'Capacity', 'Roads', ''];

const Models: React.SFC<{}> = () => {
  const [searchText, setSearchText] = React.useState('');
  return (
    <React.Fragment>
      <PageSection>
        <TextContent>
          <Text component={TextVariants.h1}>Models</Text>
        </TextContent>
      </PageSection>
      <PageSection>
        <TextContent>
          <FormGroup
            label="Search model"
            isRequired={false}
            fieldId="search-model-input"
            helperText="Please provide your full name"
          >
            <TextInput
              isRequired={true}
              type="text"
              id="simple-form-name"
              name="simple-form-name"
              aria-describedby="simple-form-name-helper"
              value={searchText}
              onChange={newValue => setSearchText(newValue)}
            />
          </FormGroup>
        </TextContent>
        <Table caption="Table with Width Modifiers" cells={columns} rows={rows}>
          <TableHeader />
          <TableBody />
        </Table>
      </PageSection>
    </React.Fragment>
  );
};

export default Models;
