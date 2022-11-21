import { shallow, toJson } from 'ui/shallow-test-util';
import LocationList, { LocationListProps } from './LocationList';

describe('Location List Component', () => {
  it('should render correctly with no routes', () => {
    const props: LocationListProps = {
      removeHandler: jest.fn(),
      selectHandler: jest.fn(),
      depot: null,
      visits: [],
    };
    const locationList = shallow(<LocationList {...props} />);
    expect(toJson(locationList)).toMatchSnapshot();
  });

  it('should render correctly with a few routes', () => {
    const props: LocationListProps = {
      removeHandler: jest.fn(),
      selectHandler: jest.fn(),
      depot: {
        id: 1,
        lat: 1.345678,
        lng: 1.345678,
        description: 'Depot',
      },
      visits: [
        {
          id: 2,
          lat: 2.345678,
          lng: 2.345678,
          description: 'Visit 1',
        },
        {
          id: 3,
          lat: 3.676111,
          lng: 3.568333,
          description: 'Visit 2',
        },
      ],
    };
    const locationList = shallow(<LocationList {...props} />);
    expect(toJson(locationList)).toMatchSnapshot();
  });
});
