import React from 'react';

import FolderItems from './../components/FolderItems';
import FeedItem from '../components/FeedItem';
import { Feed, Folder } from '../api/Model';
import Loading from '../components/Loading';
import FeedApi from '../api/FeedApi';

interface FeedListState {
    feeds: Array<Feed>;
    folders: Array<Folder>;
}

export default class FeedList extends React.Component<{}, FeedListState> {

    state = {
        feeds: [],
        folders: [],
        loading: true
    }

    componentDidMount = async () => {
        const feedList: any = await FeedApi.getFeedList();
        if (feedList) {
            this.setState({ feeds: feedList.feeds, folders: feedList.folders, loading: false });
        }
    }

    showFolders = () => {
        const { folders } = this.state;
        if (!folders || folders.length === 0) {
            return null;
        }

        return <>
            {folders.map(folder => <FolderItems folder={folder} />)}
            <li className="border-top my-3"></li>
        </>
    }

    showFeeds = () => {
        const { feeds } = this.state;
        if (!feeds || feeds.length === 0) {
            return null;
        }

        return <>
            {feeds.map(feed => <FeedItem key={feed.masterFeedID} feed={feed} />)}
        </>
    }

    render() {
        const { loading } = this.state;
        if (loading) {
            return <Loading />
        }

        return <>
            <div className="p-3 bg-white" style={{ width: '280px' }}>
                <ul className="list-unstyled ps-0">
                    {this.showFolders()}

                    {this.showFeeds()}
                </ul>
            </div>
        </>
    }

}
