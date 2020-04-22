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

import { BackgroundImage, BackgroundImageSrc } from '@patternfly/react-core';
import filter from '@patternfly/react-core/dist/styles/assets/images/background-filter.svg';
import pfBackground1200 from '@patternfly/react-core/dist/styles/assets/images/pfbg_1200.jpg';
import pfBackground576 from '@patternfly/react-core/dist/styles/assets/images/pfbg_576.jpg';
import pfBackground1152 from '@patternfly/react-core/dist/styles/assets/images/pfbg_576@2x.jpg';
import pfBackground768 from '@patternfly/react-core/dist/styles/assets/images/pfbg_768.jpg';
import pfBackground1536 from '@patternfly/react-core/dist/styles/assets/images/pfbg_768@2x.jpg';
import React from 'react';

const bgImages = {
  [BackgroundImageSrc.xs]: pfBackground576,
  [BackgroundImageSrc.sm]: pfBackground768,
  [BackgroundImageSrc.xs2x]: pfBackground1152,
  [BackgroundImageSrc.lg]: pfBackground1200,
  [BackgroundImageSrc.sm2x]: pfBackground1536,
  [BackgroundImageSrc.filter]: `${filter}#image_overlay`,
};

const Background: React.FC = () => (
  <BackgroundImage src={bgImages} />
);

export default Background;
