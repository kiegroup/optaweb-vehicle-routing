import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import { Marker } from 'react-leaflet';
import { Location } from 'store/route/types';
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

  it('should call remove handler when clicked', () => {
    const props: Props = {
      removeHandler: jest.fn(),
      isDepot: false,
      isSelected: true,
      location,
    };
    const locationMarker = shallow(<LocationMarker {...props} />);
    locationMarker.find(Marker).simulate('click');
    expect(props.removeHandler).toBeCalled();
  });
});
