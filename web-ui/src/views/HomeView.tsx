import React from 'react';
import { collect, WithStoreProp } from 'react-recollect';

import FeedList from '../containers/FeedList';
import ContentPane from '../containers/ContentPane';

interface HomeViewProps extends WithStoreProp {
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
        return <div className='d-flex flex-row'>
            {this.showFeedList()}
            {this.props.children}
        </div>
    }
}

export default collect(HomeView);
