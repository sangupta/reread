import React from 'react';
import { collect, WithStoreProp } from 'react-recollect';

import FeedList from '../containers/FeedList';
import FeedLoader, { FeedLoaderMode } from '../containers/FeedLoader';

interface HomeViewProps extends WithStoreProp {
    mode: FeedLoaderMode;
    query?: string;
}

/**
 * View rendered when the home page is opened
 */
class HomeView extends React.Component<HomeViewProps> {

    /**
     * Render the left hand side feed list if the toggle
     * is on
     */
    showFeedList = () => {
        const { store } = this.props;
        if (store.showFeedList) {
            return <FeedList />;
        }

        return null;
    }

    render() {
        const { mode, query } = this.props;
        return <div className='d-flex flex-row'>
            {this.showFeedList()}
            <div className='w-100 h-100 posts-container'>
                <FeedLoader key='feedLoader' mode={mode} query={query} />
            </div>
        </div>
    }
}

export default collect(HomeView);
