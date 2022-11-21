import { shallow, toJson } from 'ui/shallow-test-util';
import Header from './Header';

describe('Header component', () => {
  it('should match snapshot', () => {
    const header = shallow(<Header />);
    expect(toJson(header)).toMatchSnapshot();
  });
});
