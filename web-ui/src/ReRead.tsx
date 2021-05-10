import React from 'react';
import ReactDOM from 'react-dom';
import { store } from 'react-recollect';
import Axios from 'axios';

import App from './App';
import { Feed, Folder } from './api/Model';

const SERVER_URL = 'http://localhost:1309';

declare module 'react-recollect' {
    interface Store {
        showFeedList: boolean;
        layout: 'cards' | 'list';
        searchText: string;
        sortOption: string;
        displayItem: string;
    }
}

store.showFeedList = true;
store.layout = 'cards';
store.searchText = '';
store.displayItem = 'all';
store.sortOption = 'newest';

Axios.interceptors.request.use(request => {
    if (request.url?.startsWith('/')) {
        request.url = SERVER_URL + request.url;
    }
    return request;
});

ReactDOM.render(<App />, document.body);
