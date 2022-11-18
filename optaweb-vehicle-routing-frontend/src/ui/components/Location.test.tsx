import { Button, DataListItem } from '@patternfly/react-core';
import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import Location, { LocationProps } from './Location';

describe('Location Component', () => {
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
    location.find(DataListItem).simulate('mouseEnter');
    location.find(Button).simulate('click');

    expect(props.removeHandler).toHaveBeenCalledTimes(1);
    expect(props.selectHandler).toHaveBeenCalledTimes(1);
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
