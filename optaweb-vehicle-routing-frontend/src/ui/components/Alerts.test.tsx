import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import * as React from 'react';
import { Alerts, Props } from 'ui/components/Alerts';
import { shallow, toJson } from 'ui/shallow-test-util';

describe('Alerts', () => {
  it('should call readMessage() when alert is closed', async () => {
    const props: Props = {
      messages: [
        { id: '1', text: 'msg 1', status: 'new' },
        { id: '2', text: 'msg 2', status: 'new' },
      ],
      readMessage: jest.fn(),
    };
    const user = userEvent.setup();
    // TODO add a shallow render test
    const alerts = render(<Alerts {...props} />);
    expect(alerts).toMatchSnapshot();

    await user.click(screen.getAllByTitle('Close alert')[1]);

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
