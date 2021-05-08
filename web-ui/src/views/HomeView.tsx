import React from 'react';

import FeedList from '../containers/FeedList';
import ContentPane from '../containers/ContentPane';

export default class HomeView extends React.Component {

    render() {
        return <div className='d-flex flex-row'>
            <FeedList />
            <ContentPane />
        </div>
    }
}
