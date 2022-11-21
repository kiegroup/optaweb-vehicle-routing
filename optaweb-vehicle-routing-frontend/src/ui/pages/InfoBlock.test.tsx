import { shallow, toJson } from 'ui/shallow-test-util';
import { PlusIcon } from '@patternfly/react-icons';
import { CapacityInfo, DistanceInfo, InfoBlock, VehiclesInfo, VisitsInfo } from 'ui/pages/InfoBlock';

describe('Info block snapshots:', () => {
  it('generic', () => {
    const infoBlock = shallow(
      <InfoBlock
        icon={PlusIcon}
        content={{
          data: 'test content', minWidth: '10',
        }}
        tooltip="test tooltip"
      />,
    );
    expect(toJson(infoBlock)).toMatchSnapshot();
  });
  it('capacity', () => {
    const capacityInfoOK = shallow(<CapacityInfo totalDemand={20} totalCapacity={100} />);
    expect(toJson(capacityInfoOK)).toMatchSnapshot();
    const capacityInfoError = shallow(<CapacityInfo totalDemand={20} totalCapacity={10} />);
    expect(toJson(capacityInfoError)).toMatchSnapshot();
  });
  it('distance', () => {
    const distanceInfo = shallow(<DistanceInfo distance="3h 56m 11s" />);
    expect(toJson(distanceInfo)).toMatchSnapshot();
  });
  it('vehicles', () => {
    const vehiclesInfo = shallow(<VehiclesInfo />);
    expect(toJson(vehiclesInfo)).toMatchSnapshot();
  });
  it('visits', () => {
    const visitsInfo = shallow(<VisitsInfo visitCount={300} />);
    expect(toJson(visitsInfo)).toMatchSnapshot();
  });
});
