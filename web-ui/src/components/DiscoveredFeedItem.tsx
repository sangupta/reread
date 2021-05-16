import React from 'react';
import { withRouter } from 'react-router-dom';
import { RouteComponentProps } from "react-router";

import { DiscoveredFeed } from '../api/Model';
import FeedApi from '../api/FeedApi';
import Icon from './Icon';

interface DiscoveredFeedProps extends RouteComponentProps {
    feed: DiscoveredFeed;
    folder: string;
}

class DiscoveredFeedItem extends React.Component<DiscoveredFeedProps, {}> {

    addFeed = async () => {
        const data = await FeedApi.subscribeFeed(this.props.feed, this.props.folder);
        if (data && data.feedID) {
            this.props.history.push('/feed/' + data.feedID);
        }
    }

    render() {
        const { feed } = this.props;
        const hasImage = !!feed.iconUrl;
        return <div className='row px-2'>
            <div className='col-auto'>
                {hasImage && <img src={feed.iconUrl} width={16} />}
                {!hasImage && <Icon name='rss' />}
                {feed.title}
            </div>
            <div className='col-auto'>
                <button className='btn btn-sm btn-outline-primary' onClick={this.addFeed}>Add</button>
            </div>
        </div>
    }
}

export default withRouter(DiscoveredFeedItem);
