import React from 'react';
import ReactDOM from 'react-dom';
import { store } from 'react-recollect';
import Axios from 'axios';

import App from './App';
import { Feed, Folder } from './api/Model';

const SERVER_URL = 'http://localhost:1309';

declare module 'react-recollect' {
    interface Store {
        feeds: Array<Feed>;
        folders: Array<Folder>;
        showFeedList: boolean;
        layout: 'cards' | 'list';
    }
}

store.folders = [];
store.feeds = [];
store.showFeedList = true;
store.layout = 'cards';

Axios.interceptors.request.use(request => {
    if (request.url?.startsWith('/')) {
        request.url = SERVER_URL + request.url;
    }
    return request;
});

ReactDOM.render(<App />, document.body);
