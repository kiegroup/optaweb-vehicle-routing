import { Alert } from '@patternfly/react-core';
import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import * as React from 'react';
import { Alerts, Props } from 'ui/components/Alerts';

describe('Alerts', () => {
  it('should call readMessage() when alert is closed', () => {
    const props: Props = {
      messages: [
        { id: '1', text: 'msg 1', status: 'new' },
        { id: '2', text: 'msg 2', status: 'new' },
      ],
      readMessage: jest.fn(),
    };
    const alerts = shallow(<Alerts {...props} />);
    expect(toJson(alerts)).toMatchSnapshot();

    (alerts.find(Alert).at(1).prop('actionClose') as React.ReactElement).props.onClose();

    expect(props.readMessage).toHaveBeenCalledWith('2');
  });

  it('should not render if there are no messages', () => {
    const props: Props = {
      messages: [],
      readMessage: jest.fn(),
    };
    const alerts = shallow(<Alerts {...props} />);
    expect(toJson(alerts)).toMatchSnapshot();
  });
});
