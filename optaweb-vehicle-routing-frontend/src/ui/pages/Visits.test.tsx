import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import * as React from 'react';
import { Props, Visits } from './Visits';

describe('Visits page', () => {
  it('should render correctly with no visits', () => {
    const visits = shallow(<Visits {...noVisits} />);
    expect(toJson(visits)).toMatchSnapshot();
  });

  it('should render correctly with a few visits', () => {
    const visits = shallow(<Visits {...twoVisits} />);
    expect(toJson(visits)).toMatchSnapshot();
  });
});

const noVisits: Props = {
  addHandler: jest.fn(),
  removeHandler: jest.fn(),

  depot: null,
  visits: [],
};

const twoVisits: Props = {
  addHandler: jest.fn(),
  removeHandler: jest.fn(),

  depot: {
    id: 1,
    lat: 1.345678,
    lng: 1.345678,
  },

  visits: [{
    id: 2,
    lat: 2.345678,
    lng: 2.345678,
  }, {
    id: 3,
    lat: 3.676111,
    lng: 3.568333,
  }],
};
