import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import * as React from 'react';
import Header from './Header';

describe('Header component', () => {
  it('should match snapshot', () => {
    const header = shallow(<Header />);
    expect(toJson(header)).toMatchSnapshot();
  });
});
