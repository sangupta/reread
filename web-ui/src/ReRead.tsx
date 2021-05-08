import React from 'react';
import ReactDOM from 'react-dom';
import { store } from 'react-recollect';

import App from './App';

declare module 'react-recollect' {
    interface Store {
        feeds: Array<any>;
        showFeedList: boolean;
    }
}

store.feeds = [];
store.showFeedList = true;

ReactDOM.render(<App />, document.body);
