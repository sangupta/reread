import React from 'react';
import { withRouter } from 'react-router-dom';

import { Feed } from '../api/Model';
import Icon from './Icon';

interface FeedItemProps {
    feed: Feed;
    history: any;
    mode?: string;
}

class FeedItem extends React.Component<FeedItemProps, any> {

    showFeed = () => {
        const { feed, history, mode } = this.props;
        const path = 'folder' === mode ? '/folder/' : '/feed/';
        history.push(path + feed.masterFeedID);
    }

    render() {
        const { feed } = this.props;

        return <li>
            <a href='#' className='link-dark rounded' onClick={this.showFeed}>
                <Icon name='rss' label={feed.title} />
            </a>
        </li>
    }

}

export default withRouter(FeedItem);
