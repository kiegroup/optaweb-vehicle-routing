import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import * as React from 'react';
import App from './App';

describe('App', () => {
  it('should render correctly', () => {
    const app = shallow(<App />);
    expect(toJson(app)).toMatchSnapshot();
  });
});
