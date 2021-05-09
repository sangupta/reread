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
        return <div className='d-flex flex-row mt-3'>
            {this.showFeedList()}
            <div className='w-100 h-100'>
                {this.props.children}
            </div>
        </div>
    }
}

export default collect(HomeView);
