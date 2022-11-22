import { BackgroundImage } from '@patternfly/react-core';
import pfBackground1200 from '@patternfly/react-core/dist/styles/assets/images/pfbg_1200.jpg';
import pfBackground576 from '@patternfly/react-core/dist/styles/assets/images/pfbg_576.jpg';
import pfBackground1152 from '@patternfly/react-core/dist/styles/assets/images/pfbg_576@2x.jpg';
import pfBackground768 from '@patternfly/react-core/dist/styles/assets/images/pfbg_768.jpg';
import pfBackground1536 from '@patternfly/react-core/dist/styles/assets/images/pfbg_768@2x.jpg';
import * as React from 'react';

const images = {
  xs: pfBackground576,
  sm: pfBackground768,
  xs2x: pfBackground1152,
  lg: pfBackground1200,
  sm2x: pfBackground1536,
};

const Background: React.FC = () => <BackgroundImage src={images} />;

export default Background;
