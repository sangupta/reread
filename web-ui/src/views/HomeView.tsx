import React from 'react';
import { collect } from 'react-recollect';

import FeedList from '../containers/FeedList';
import ContentPane from '../containers/ContentPane';

class HomeView extends React.Component {

    showFeedList = () => {
        const { store } = this.props;
        if (store.showFeedList) {
            return <FeedList />;
        }

        return null;
    }

    render() {
        return <div className='d-flex flex-row'>
            {this.showFeedList()}
            <ContentPane />
        </div>
    }
}

export default collect(HomeView);
