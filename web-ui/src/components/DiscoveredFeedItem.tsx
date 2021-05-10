import React from 'react';
import { withRouter } from 'react-router-dom';

import { DiscoveredFeed } from '../api/Model';
import FeedApi from '../api/FeedApi';
import Icon from './Icon';

interface DiscoveredFeedProps {
    feed: DiscoveredFeed;
    history: any;
}

class DiscoveredFeedItem extends React.Component<DiscoveredFeedProps, {}> {

    addFeed = async () => {
        const data = await FeedApi.subscribeFeed(this.props.feed);
        if (data && data.feedID) {
            this.props.history.push('/feed/' + data.feedID);
        }
    }

    render() {
        const { feed } = this.props;

        return <div className='row'>
            <div className='col-auto'>
                <Icon name='rss' />&nbsp;{feed.title}
            </div>
            <div className='col-auto'>
                <button className='btn btn-sm btn-outline-primary' onClick={this.addFeed}>Add</button>
            </div>
        </div>
    }
}

export default withRouter(DiscoveredFeedItem);
