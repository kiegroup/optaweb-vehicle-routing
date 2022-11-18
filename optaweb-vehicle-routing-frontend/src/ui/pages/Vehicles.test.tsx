import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import { Props, Vehicles } from './Vehicles';

describe('Vehicles page', () => {
  it('should render correctly', () => {
    const props: Props = {
      addVehicleHandler: jest.fn(),
      removeVehicleHandler: jest.fn(),
      changeVehicleCapacityHandler: jest.fn,
      vehicles: [
        { id: 1, name: 'Vehicle 1', capacity: 5 },
        { id: 2, name: 'Vehicle 2', capacity: 5 },
      ],
    };
    const vehicles = shallow(<Vehicles {...props} />);
    expect(toJson(vehicles)).toMatchSnapshot();
  });
});
