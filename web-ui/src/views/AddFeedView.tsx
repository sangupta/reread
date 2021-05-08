import React from 'react';

import DiscoveredFeed from './../components/DiscoveredFeed';
import FeedApi from '../api/FeedApi';
import { Feed } from '../api/Model';

interface AddFeedViewState {
    url: string;
    feeds: Array<Feed> | null;
}

/**
 * View used when add feed button is clicked in the top bar
 */
export default class AddFeedView extends React.Component<{}, AddFeedViewState> {

    /**
     * Default state
     */
    state = {
        url: '',
        feeds: null
    };

    /**
     * Set the URL when text box is edited
     */
    setUrl = (e) => {
        this.setState({ url: e.target.value });
    }

    setOpmlFile = (e) => {

    }

    /**
     * Handler invoked when discover button is clicked
     */
    discoverFeed = async () => {
        const { url } = this.state;
        if (url) {
            const feeds = await FeedApi.discoverFeed(url);
            this.setState({ feeds: feeds });
        }
    }

    importOpml = () => {

    }

    showDiscoveredFeeds = () => {
        const { feeds } = this.state;
        if (!feeds) {
            return null;
        }

        if (feeds.length === 0) {
            return <div className='alert alert-info mt-3'>No feeds were discovered.</div>
        }

        return <div className='mt-3'>
            <h2>Discovered Feeds</h2>
            {feeds.map(feed => <DiscoveredFeed feed={feed} /> )}
        </div>

    }

    render() {
        return <div className='row mt-3'>
            <div className='col'>
                <h1>Add site/feed</h1>

                <form autoComplete='off' className='mt-3'>
                    <div className="form-group">
                        <label htmlFor="siteAddress">Site Address</label>
                        <input id='siteAddress' type="url" className="form-control md-6" aria-describedby="siteAddressHelp" placeholder="https://site-to-follow.com" onChange={this.setUrl} />
                        <small id="siteAddressHelp" className="form-text text-muted">We will try and find RSS and social media you can follow from the site.</small>
                    </div>
                    <button type='button' className='btn btn-primary mt-3' onClick={this.discoverFeed}>Discover</button>
                </form>
                {this.showDiscoveredFeeds()}
            </div>

            <div className='col'>
                <h1 className='mt-3'>Import OPML file</h1>
                <form autoComplete='off' className='mt-3'>
                    <div className="form-group">
                        <label htmlFor="opmlFile">OPML file</label>
                        <input id='opmlFile' type="file" className="form-control md-6" aria-describedby="opmlFileHelp" onChange={this.setOpmlFile} />
                        <small id="opmlFileHelp" className="form-text text-muted">Any existing feed present in OPML file will be ignored.</small>
                    </div>
                    <button type='button' className='btn btn-primary mt-3' onClick={this.importOpml}>Import</button>
                </form>
            </div>
        </div>
    }
}
