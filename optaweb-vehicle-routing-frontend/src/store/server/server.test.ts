import * as actions from './actions';
import reducer from './index';
import { initialServerState } from './reducers';
import { ServerInfo } from './types';

describe('Server reducer', () => {
  const serverInfo: ServerInfo = {
    boundingBox: null,
    countryCodes: ['CZ', 'SK'],
    demos: [{ name: 'Demo name', visits: 10 }],
  };

  it('server info', () => {
    expect(
      reducer(initialServerState, actions.serverInfo(serverInfo)),
    ).toEqual(serverInfo);
  });
});
