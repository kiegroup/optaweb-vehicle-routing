import { render } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Location } from 'store/route/types';
import { shallow, toJson } from 'ui/shallow-test-util';
import LocationMarker, { Props } from './LocationMarker';

const location: Location = {
  id: 1,
  lat: 1.345678,
  lng: 1.345678,
};

describe('Location Marker', () => {
  it('render depot', () => {
    const props: Props = {
      removeHandler: jest.fn(),
      isDepot: true,
      isSelected: false,
      location,
    };
    const locationMarker = shallow(<LocationMarker {...props} />);
    expect(toJson(locationMarker)).toMatchSnapshot();
  });

  it('render visit', () => {
    const props: Props = {
      removeHandler: jest.fn(),
      isDepot: false,
      isSelected: false,
      location,
    };
    const locationMarker = shallow(<LocationMarker {...props} />);
    expect(toJson(locationMarker)).toMatchSnapshot();
  });

  it('selected visit should show a tooltip', () => {
    const props: Props = {
      removeHandler: jest.fn(),
      isDepot: false,
      isSelected: true,
      location,
    };
    const locationMarker = shallow(<LocationMarker {...props} />);
    expect(toJson(locationMarker)).toMatchSnapshot();
  });

  xit('should call remove handler when clicked', async () => {
    const props: Props = {
      removeHandler: jest.fn(),
      isDepot: false,
      isSelected: true,
      location,
    };
    // FIXME Cannot read property 'addLayer' of undefined
    render(<LocationMarker {...props} />);

    userEvent.setup();
    // TODO
    // await user.click(screen.find(Marker));
    expect(props.removeHandler).toBeCalled();
  });
});
