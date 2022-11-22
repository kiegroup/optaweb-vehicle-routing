import { ReactElement } from 'react';
// eslint-disable-next-line import/no-extraneous-dependencies
import { createRenderer } from 'react-test-renderer/shallow';

export const shallow = (e: ReactElement): ReactElement => {
  const shallowRenderer = createRenderer();
  shallowRenderer.render(e);
  return shallowRenderer.getRenderOutput();
};

export const toJson = (e: ReactElement): ReactElement => e;
