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
  BackgroundImage,
  BackgroundImageSrc,
  BackgroundImageSrcMap,
  Brand,
} from '@patternfly/react-core';
import * as React from 'react';

const defaultProps: IOVRThemeProviderProps = {
  bgImages: {
    [BackgroundImageSrc.lg]: '/assets/images/pfbg_1200.jpg',
    [BackgroundImageSrc.sm]: '/assets/images/pfbg_768.jpg',
    [BackgroundImageSrc.sm2x]: '/assets/images/pfbg_768@2x.jpg',
    [BackgroundImageSrc.xs]: '/assets/images/pfbg_576.jpg',
    [BackgroundImageSrc.xs2x]: '/assets/images/pfbg_576@2x.jpg',
    [BackgroundImageSrc.filter]:
      '/assets/images/background-filter.svg#image_overlay',
  },
  brandImg: '/assets/images/optaPlannerLogo200px.png',
};

const PatternFlyContext = React.createContext<
  IOVRThemeProviderState & IOVRThemeProviderProps
>({
  ...defaultProps,
  components: {
    Background: undefined,
    Brand: undefined,
  },
});

export interface IOVRThemeProviderProps {
  bgImages?: BackgroundImageSrcMap | string;
  brandImg?: string;
}

export interface IOVRThemeProviderState {
  components: {
    Brand?: React.ReactNode;
    Background?: React.ReactNode;
  };
}

class OVRThemeProvider extends React.Component<
  IOVRThemeProviderProps,
  IOVRThemeProviderState
> {
  static defaultProps = defaultProps;
  constructor(props: IOVRThemeProviderProps) {
    super(props);
  }
  componentDidMount() {
    this.setState({
      components: {
        Background: <BackgroundImage src={this.props.bgImages!} />,
        Brand: <Brand src={this.props.brandImg} alt="Patternfly Logo" />,
      },
    });
  }
  render() {
    return (
      <PatternFlyContext.Provider value={{ ...this.state, ...this.props }}>
        {this.props.children}
      </PatternFlyContext.Provider>
    );
  }
}
export default OVRThemeProvider;

export const OVRThemeConsumer = PatternFlyContext.Consumer;
