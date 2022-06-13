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
