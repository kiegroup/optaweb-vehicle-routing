import { shallow, toJson } from 'ui/shallow-test-util';
import App from './App';

describe('App', () => {
  it('should render correctly', () => {
    const app = shallow(<App />);
    expect(toJson(app)).toMatchSnapshot();
  });
});
