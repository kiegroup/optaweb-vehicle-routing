import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { VehicleCapacity } from 'store/route/types';
import { shallow, toJson } from 'ui/shallow-test-util';
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
  });

  it('handlers', async () => {
    const props: VehicleProps = {
      id: 10,
      description: 'x',
      capacity: 7,
      removeHandler: jest.fn(),
      capacityChangeHandler: jest.fn(),
    };
    render(<Vehicle {...props} />);

    const user = userEvent.setup();

    await user.click(screen.getByTestId(`remove-${props.id}`));
    expect(props.removeHandler).toHaveBeenCalledTimes(1);

    await user.click(screen.getByTestId(`capacity-increase-${props.id}`));
    const increasedCapacity: VehicleCapacity = {
      vehicleId: props.id,
      capacity: props.capacity + 1,
    };
    expect(props.capacityChangeHandler).toHaveBeenCalledWith(increasedCapacity);

    await user.click(screen.getByTestId(`capacity-decrease-${props.id}`));
    const decreasedCapacity: VehicleCapacity = {
      vehicleId: props.id,
      capacity: props.capacity - 1,
    };
    expect(props.capacityChangeHandler).toHaveBeenCalledWith(decreasedCapacity);
  });
});
