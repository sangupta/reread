import React from 'react';
import ReactDOM from 'react-dom';
import { store } from 'react-recollect';
import Axios from 'axios';

import App from './App';

const SERVER_URL = 'http://localhost:1309';

declare module 'react-recollect' {
    interface Store {
        feeds: Array<any>;
        folders: Array<any>;
        showFeedList: boolean;
    }
}

store.folders = [];
store.feeds = [];
store.showFeedList = true;

Axios.interceptors.request.use(request => {
    if (request.url?.startsWith('/')) {
        request.url = SERVER_URL + request.url;
    }
    return request;
});

ReactDOM.render(<App />, document.body);
