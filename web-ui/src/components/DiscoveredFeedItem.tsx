import React from 'react';
import { DiscoveredFeed } from '../api/Model';
import FeedApi from '../api/FeedApi';

interface DiscoveredFeedProps {
    feed: DiscoveredFeed;
}

export default class DiscoveredFeedItem extends React.Component<DiscoveredFeedProps> {

    addFeed = async () => {
        const data = await FeedApi.subscribeFeed(this.props.feed);
    }

    render() {
        const { feed } = this.props;

        return <div className='row'>
            <div className='col-auto mx-1'>&nbsp;</div>
            <div className='col-auto'>{feed.title}</div>
            <div className='col-auto'>
                <button className='btn btn-sm btn-outline-primary' onClick={this.addFeed}>Add</button>
            </div>
        </div>
    }
}