import { configure } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import EventSource from 'eventsourcemock';

configure({ adapter: new Adapter() });

Object.defineProperty(window, 'EventSource', {
  value: EventSource,
});
