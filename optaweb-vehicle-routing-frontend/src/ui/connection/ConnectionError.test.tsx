import { shallow, toJson } from 'ui/shallow-test-util';
import ConnectionError, { ConnectionErrorProps } from './ConnectionError';

describe('Connection Error Component', () => {
  it('should render correctly', () => {
    const props: ConnectionErrorProps = {
      isOpen: true,
    };

    const connectionError = shallow(<ConnectionError {...props} />);
    expect(toJson(connectionError)).toMatchSnapshot();
  });
});
