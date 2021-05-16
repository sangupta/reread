import React from 'react';
import { withRouter } from 'react-router-dom';
import { RouteComponentProps } from "react-router";

import { Feed } from '../api/Model';
import Icon from './Icon';

interface FeedItemProps extends RouteComponentProps {
    feed: Feed;
    mode?: string;
    onFeedSelect: (feedID: string) => void;
    highlight: string;
}

class FeedItem extends React.Component<FeedItemProps, any> {

    showFeed = () => {
        const { feed, history, mode } = this.props;
        const path = 'folder' === mode ? '/folder/' : '/feed/';
        history.push(path + feed.masterFeedID);
        this.props.onFeedSelect(feed.masterFeedID);
    }

    render() {
        const { feed, highlight } = this.props;
        const hasImage = !!feed.iconUrl;
        const css:string = (feed.masterFeedID === highlight) ? 'feed-active' : ''
        return <li className={css}>
            <a href='#' className='link-dark rounded feed-list-link' onClick={this.showFeed}>
                {hasImage && <img className='feed-list-icon' src={feed.iconUrl} />}
                {!hasImage && <Icon name='rss' />}
                {feed.title}
            </a>
        </li>
    }

}

export default withRouter(FeedItem);
