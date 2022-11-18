import '@patternfly/react-core/dist/styles/base.css';
import { backendUrl } from 'common';
import * as ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import './index.css';
import { unregister } from './registerServiceWorker';
import { configureStore } from './store';
import App from './ui/App';

const store = configureStore({
  backendUrl: `${backendUrl}/api`,
});

ReactDOM.render(
  <Provider store={store}>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </Provider>,
  document.getElementById('root') as HTMLElement,
);

unregister();
