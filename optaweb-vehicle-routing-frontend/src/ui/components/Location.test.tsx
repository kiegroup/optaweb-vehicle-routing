import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { shallow, toJson } from 'ui/shallow-test-util';
import Location, { LocationProps } from './Location';

describe('Location Component', () => {
  it('should call handlers', async () => {
    const props: LocationProps = {
      id: 10,
      description: 'x',
      removeDisabled: false,
      removeHandler: jest.fn(),
      selectHandler: jest.fn(),
    };
    render(<Location {...props} />);

    const user = userEvent.setup();

    await user.click(screen.getByRole('button'));

    expect(props.removeHandler).toHaveBeenCalledTimes(1);
    expect(props.selectHandler).toHaveBeenCalledTimes(1);
  });

  it('should render correctly', () => {
    const props: LocationProps = {
      id: 10,
      description: 'x',
      removeDisabled: false,
      removeHandler: jest.fn(),
      selectHandler: jest.fn(),
    };
    const location = shallow(<Location {...props} />);
    expect(toJson(location)).toMatchSnapshot();
  });

  it('should render correctly when description is missing', () => {
    const props: LocationProps = {
      id: 11,
      description: null,
      removeDisabled: false,
      removeHandler: jest.fn(),
      selectHandler: jest.fn(),
    };
    const location = shallow(<Location {...props} />);
    expect(toJson(location)).toMatchSnapshot();
  });
});
