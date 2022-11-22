import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { UserViewport } from 'store/client/types';
import { shallow, toJson } from 'ui/shallow-test-util';
import { Demo, DemoProps } from './Demo';

describe('Demo page', () => {
  it('should render correctly with no routes', () => {
    const demo = shallow(<Demo {...emptyRouteProps} />);
    expect(toJson(demo)).toMatchSnapshot();
  });

  it('should render correctly with a few routes', () => {
    const demo = shallow(<Demo {...threeLocationsProps} />);
    expect(toJson(demo)).toMatchSnapshot();
  });

  it('clear and export buttons should be disabled when demo is loading', () => {
    const props: DemoProps = {
      ...threeLocationsProps,
      isDemoLoading: true,
    };
    const demo = shallow(<Demo {...props} />);
    expect(toJson(demo)).toMatchSnapshot();
  });

  // FIXME
  xit('clear and export buttons should be disabled when demo is loading', async () => {
    const props: DemoProps = {
      ...threeLocationsProps,
      isDemoLoading: true,
    };
    const user = userEvent.setup();
    render(<Demo {...props} />);

    const clearButton = screen.getByRole('button', { name: 'Clear' });
    expect(clearButton).toBeDisabled();

    await user.click(clearButton);
    // Doesn't work, probably due to https://github.com/airbnb/enzyme/issues/386
    expect(props.clearHandler).not.toHaveBeenCalled();

    const exportButton = screen.getByRole('button', { name: 'Export' });
    expect(exportButton).toBeDisabled();
  });

  it('clear button should replace demo dropdown as soon as there is a depot', () => {
    const props: DemoProps = {
      ...emptyRouteProps,
      depot: {
        id: 1,
        lat: 1,
        lng: 1,
        description: '',
      },
    };
    render(<Demo {...props} />);

    const clearButton = screen.getByRole('button', { name: 'Clear' });
    expect(clearButton).toBeEnabled();

    const exportButton = screen.getByRole('button', { name: 'Export' });
    expect(exportButton).toBeEnabled();

    expect(screen.queryByRole('button', { name: 'Load demo' })).not.toBeInTheDocument();
  });
});

const userViewport: UserViewport = {
  isDirty: false,
  zoom: 1,
  center: [0, 0],
};

const emptyRouteProps: DemoProps = {
  loadHandler: jest.fn(),
  clearHandler: jest.fn(),
  addVehicleHandler: jest.fn,
  removeVehicleHandler: jest.fn,
  addLocationHandler: jest.fn(),
  removeLocationHandler: jest.fn(),
  updateViewport: jest.fn(),

  distance: '0',
  vehicleCount: 0,
  totalCapacity: 0,
  totalDemand: 0,
  demoNames: ['demo'],
  isDemoLoading: false,
  boundingBox: null,
  userViewport,
  countryCodeSearchFilter: [],

  depot: null,
  routes: [],
  visits: [],
};

const threeLocationsProps: DemoProps = {
  loadHandler: jest.fn(),
  clearHandler: jest.fn(),
  addVehicleHandler: jest.fn,
  removeVehicleHandler: jest.fn,
  addLocationHandler: jest.fn(),
  removeLocationHandler: jest.fn(),
  updateViewport: jest.fn(),

  distance: '10',
  vehicleCount: 8,
  totalCapacity: 5,
  totalDemand: 2,
  demoNames: ['demo'],
  isDemoLoading: false,
  boundingBox: null,
  userViewport,
  countryCodeSearchFilter: ['XY'],

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

  routes: [{
    vehicle: { id: 1, name: 'v1', capacity: 5 },
    visits: [{
      id: 1,
      lat: 1.345678,
      lng: 1.345678,
    }, {
      id: 2,
      lat: 2.345678,
      lng: 2.345678,
    }, {
      id: 3,
      lat: 3.676111,
      lng: 3.568333,
    }],

    track: [],

  }],
};
