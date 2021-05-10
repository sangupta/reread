import React from 'react';

import DiscoveredFeedItem from './../components/DiscoveredFeedItem';
import FeedApi from '../api/FeedApi';
import { DiscoveredFeed } from '../api/Model';

interface AddFeedContainerState {
    url: string;
    feeds: Array<DiscoveredFeed>;
    discovered: boolean;
}

export default class AddFeedContainer extends React.Component<{}, AddFeedContainerState> {

    state = {
        url: 'https://news.ycombinator.com/rss',
        feeds: [],
        discovered: false,
    };

    /**
     * Set the URL when text box is edited
     */
    setUrl = (e) => {
        this.setState({ url: e.target.value });
    }

    /**
     * Handler invoked when discover button is clicked
     */
    discoverFeed = async () => {
        const { url } = this.state;
        if (url) {
            const feeds = await FeedApi.discoverFeed(url);
            this.setState({ feeds: feeds, discovered: true });
        }
    }

    showDiscoveredFeeds = () => {
        const { discovered, feeds } = this.state;
        if (!discovered) {
            return null;
        }

        if (feeds.length === 0) {
            return <div className='alert alert-info mt-3'>No feeds were discovered.</div>
        }

        return <div className='mt-3'>
            <h2>Discovered Feeds</h2>
            {feeds.map(feed => <DiscoveredFeedItem key={feed.url} feed={feed} />)}
        </div>
    }

    render() {
        return <>
            <h1>Add site/feed</h1>

            <form autoComplete='off' className='mt-3'>
                <div className="form-group">
                    <label htmlFor="siteAddress">Site Address</label>
                    <input id='siteAddress' type="url" className="form-control md-6" aria-describedby="siteAddressHelp" placeholder="https://site-to-follow.com" value={this.state.url} onChange={this.setUrl} />
                    <small id="siteAddressHelp" className="form-text text-muted">We will try and find RSS and social media you can follow from the site.</small>
                </div>
                <button type='button' className='btn btn-primary mt-3' onClick={this.discoverFeed}>Discover</button>
            </form>
            {this.showDiscoveredFeeds()}
        </>
    }
}