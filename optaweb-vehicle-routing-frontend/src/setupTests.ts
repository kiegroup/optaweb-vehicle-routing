import Enzyme from 'enzyme';
import Adapter from '@wojtekmaj/enzyme-adapter-react-17';
import EventSource from 'eventsourcemock';

Enzyme.configure({ adapter: new Adapter() });

Object.defineProperty(window, 'EventSource', {
  value: EventSource,
});
