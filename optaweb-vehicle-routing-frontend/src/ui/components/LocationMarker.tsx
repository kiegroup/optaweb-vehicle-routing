import * as L from 'leaflet';
import * as React from 'react';
import { Marker, Tooltip } from 'react-leaflet';
import { Location } from 'store/route/types';

const homeIcon = L.icon({
  iconAnchor: [12, 12],
  iconSize: [24, 24],
  iconUrl: 'if_big_house-home_2222740.png',
  popupAnchor: [0, -10],
  shadowAnchor: [16, 2],
  shadowSize: [50, 16],
  shadowUrl: 'if_big_house-home_2222740_shadow.png',
});

const defaultIcon = new L.Icon.Default();

export interface Props {
  location: Location;
  isDepot: boolean;
  isSelected: boolean;
  removeHandler: (id: number) => void;
}

const LocationMarker: React.FC<Props> = ({
  location,
  isDepot,
  isSelected,
  removeHandler,
}) => {
  const icon = isDepot ? homeIcon : defaultIcon;
  return (
    <Marker
      key={location.id}
      position={location}
      icon={icon}
      onClick={() => removeHandler(location.id)}
    >
      <Tooltip
        // `permanent` is a static property (this is a React-Leaflet-specific
        // approach: https://react-leaflet.js.org/docs/en/components). Changing `permanent` prop
        // doesn't result in calling `setPermanent()` on the Leaflet element after the Tooltip component is mounted.
        // We're using `key` to force re-rendering of Tooltip when `isSelected` changes. A similar use case for
        // the `key` property is described here:
        // https://reactjs.org/blog/2018/06/07/you-probably-dont-need-derived-state.html
        // #recommendation-fully-uncontrolled-component-with-a-key
        key={isSelected ? 'selected' : ''}
        permanent={isSelected}
      >
        {`Location ${location.id} [Lat=${location.lat}, Lng=${location.lng}]`}
      </Tooltip>
    </Marker>
  );
};

export default LocationMarker;
