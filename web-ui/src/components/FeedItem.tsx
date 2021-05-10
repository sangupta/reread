import React from 'react';
import { withRouter } from 'react-router-dom';

import { Feed } from '../api/Model';

interface FeedItemProps {
    feed: Feed;
    history: any;
}

class FeedItem extends React.Component<FeedItemProps, any> {

    showFeed = () => {
        const { feed, history } = this.props;
        history.push('/feed/' + feed.masterFeedID);
    }

    render() {
        const { feed } = this.props;

        return <li>
            <a href='#' className='link-dark rounded' onClick={this.showFeed}>{feed.title}</a>
        </li>
    }

}

export default withRouter(FeedItem);
