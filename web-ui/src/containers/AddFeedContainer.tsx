import React from 'react';

import DiscoveredFeedItem from './../components/DiscoveredFeedItem';

import FeedApi from '../api/FeedApi';
import { DiscoveredFeed, Folder } from '../api/Model';
import Dropdown, { DropDownOption } from '../components/Dropdown';

interface AddFeedContainerState {
    url: string;
    feeds: Array<DiscoveredFeed>;
    discovered: boolean;
    folders: Array<Folder>;
    folder: string;
}

export default class AddFeedContainer extends React.Component<{}, AddFeedContainerState> {

    state: AddFeedContainerState = {
        url: '',
        feeds: new Array<DiscoveredFeed>(),
        discovered: false,
        folders: new Array<Folder>(),
        folder: ''
    };

    componentDidMount = async () => {
        const feedList: any = await FeedApi.getFeedList();
        if (feedList) {
            this.setState({ folders: feedList.folders });
        }
    }

    /**
     * Set the URL when text box is edited
     */
    setUrl = (e: React.FormEvent<HTMLInputElement>) => {
        this.setState({ url: e.currentTarget.value });
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

    setFolder = (v: string) => {
        this.setState({ folder: v });
    }

    showFolders = () => {
        const { folders, folder } = this.state;
        const options: Array<DropDownOption> = [
            { label: '<No folder>', value: '' }
        ];

        folders.forEach((item: Folder) => {
            options.push({ label: item.title, value: item.folderID });
        });

        return <div className='d-flex flex-row'>
            <div className='px-2'>Choose folder to add feed to: </div>
            <Dropdown variant='secondary' options={options} onSelect={this.setFolder} value={folder} />
        </div>
    }

    showDiscoveredFeeds = () => {
        const { discovered, feeds, folder } = this.state;
        if (!discovered) {
            return null;
        }

        if (feeds.length === 0) {
            return <div className='alert alert-info mt-3'>No feeds were discovered.</div>
        }

        return <div className='mt-3'>
            <h2>Discovered Feeds</h2>
            <div className='my-3' />
            {this.showFolders()}
            <div className='my-3' />
            {feeds.map(feed => <DiscoveredFeedItem key={feed.feedUrl} feed={feed} folder={folder} />)}
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