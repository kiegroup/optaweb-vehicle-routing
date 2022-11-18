import { Button } from '@patternfly/react-core';
import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import { VehicleCapacity } from 'store/route/types';
import Vehicle, { VehicleProps } from './Vehicle';

describe('Vehicle Component', () => {
  it('should render correctly', () => {
    const props: VehicleProps = {
      id: 10,
      description: 'x',
      capacity: 7,
      removeHandler: jest.fn(),
      capacityChangeHandler: jest.fn(),
    };
    const vehicle = shallow(<Vehicle {...props} />);
    expect(toJson(vehicle)).toMatchSnapshot();

    vehicle.find(Button).filter(`[data-test-key="remove-${props.id}"]`).simulate('click');
    expect(props.removeHandler).toHaveBeenCalledTimes(1);

    vehicle.find(Button).filter(`[data-test-key="capacity-increase-${props.id}"]`).simulate('click');
    const increasedCapacity: VehicleCapacity = { vehicleId: props.id, capacity: props.capacity + 1 };
    expect(props.capacityChangeHandler).toHaveBeenCalledWith(increasedCapacity);

    vehicle.find(Button).filter(`[data-test-key="capacity-decrease-${props.id}"]`).simulate('click');
    const decreasedCapacity: VehicleCapacity = { vehicleId: props.id, capacity: props.capacity - 1 };
    expect(props.capacityChangeHandler).toHaveBeenCalledWith(decreasedCapacity);
  });
});
