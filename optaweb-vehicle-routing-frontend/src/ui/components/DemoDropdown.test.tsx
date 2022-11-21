import { shallow, toJson } from 'ui/shallow-test-util';
import { DemoDropdown, Props } from './DemoDropdown';

describe('Demo dropdown button', () => {
  it('should render correctly with a couple of demos', () => {
    const props: Props = {
      demos: ['demo 1', 'demo 2'],
      onSelect: jest.fn(),
    };
    const dropdown = shallow(<DemoDropdown {...props} />);
    expect(toJson(dropdown)).toMatchSnapshot();
  });

  it('should be disabled with empty demos', () => {
    const props: Props = {
      demos: [],
      onSelect: jest.fn(),
    };
    const dropdown = shallow(<DemoDropdown {...props} />);
    expect(toJson(dropdown)).toMatchSnapshot();
  });
});
