import React from 'react';
import { withRouter } from 'react-router-dom';
import { RouteComponentProps } from "react-router";

import FolderItems from './../components/FolderItems';
import FeedItem from '../components/FeedItem';
import { Feed, Folder } from '../api/Model';
import Loading from '../components/Loading';
import FeedApi from '../api/FeedApi';
import Icon from '../components/Icon';

interface FeedListProps extends RouteComponentProps {
}

interface FeedListState {
    feeds: Array<Feed>;
    folders: Array<Folder>;
    loading: boolean;
    currentFeed: string;
}

class FeedList extends React.Component<FeedListProps, FeedListState> {

    state: FeedListState = {
        feeds: [],
        folders: [],
        loading: true,
        currentFeed: '$all'
    }

    componentDidMount = () => {
        this.fetchList();
    }

    fetchList = async () => {
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
            {folders.map(folder => <FolderItems key={folder.folderID} folder={folder} onFeedSelect={this.setCurrentFeed} highlight={this.state.currentFeed} />)}
            <li className="border-top my-3"></li>
        </>
    }

    showFeeds = () => {
        const { feeds } = this.state;
        if (!feeds || feeds.length === 0) {
            return null;
        }

        return <>
            {feeds.map(feed => <FeedItem key={feed.masterFeedID} feed={feed} onFeedSelect={this.setCurrentFeed} highlight={this.state.currentFeed} />)}
        </>
    }

    showAllFeeds = () => {
        this.props.history.push('/feeds/all');
        this.setState({ currentFeed: '$all' });
    }

    showStars = () => {
        this.props.history.push('/feeds/stars')
        this.setState({ currentFeed: '$stars' });
    }

    showBookmarks = () => {
        this.props.history.push('/feeds/bookmarks')
        this.setState({ currentFeed: '$bookmarks' });
    }

    exportOpml = async (e: React.MouseEvent) => {
        e.preventDefault();

        await FeedApi.exportOpml();
    }

    createFolder = async (e: React.MouseEvent) => {
        e.preventDefault();

        const folderName = window.prompt('Enter the name of the folder: ', '');
        if (!folderName) {
            return;
        }

        const data = await FeedApi.createFolder(folderName);
        this.fetchList();
    }

    setCurrentFeed = (feedID: string) => {
        console.log('current: ', feedID);
        this.setState({ currentFeed: feedID });
    }

    render() {
        const { loading, currentFeed } = this.state;
        if (loading) {
            return <Loading />
        }

        return <>
            <div className="bg-white feed-list">
                <ul className="list-unstyled ps-0">
                    <li className="mb-1">
                        <ul className="btn-toggle-nav list-unstyled fw-normal pb-1 small">
                            <li className={(currentFeed === '$all' ? 'feed-active' : '')}>
                                <a href='#' className='link-dark rounded' onClick={this.showAllFeeds}>
                                    <Icon name='basket' label='All' />
                                </a>
                            </li>
                            <li className={(currentFeed === '$stars' ? 'feed-active' : '')}>
                                <a href='#' className='link-dark rounded' onClick={this.showStars}>
                                    <Icon name='star' label='Stars' />
                                </a>
                            </li>
                            <li className={(currentFeed === '$bookmarks' ? 'feed-active' : '')}>
                                <a href='#' className='link-dark rounded' onClick={this.showBookmarks}>
                                    <Icon name='bookmark' label='Bookmarks' />
                                </a>
                            </li>
                        </ul>
                    </li>

                    <li className="border-top my-3"></li>

                    <li>
                        <ul className="btn-toggle-nav list-unstyled fw-normal pb-1 small">
                            <li>
                                <a href='#' className='link-dark rounded' onClick={this.createFolder}>
                                    <Icon name='folder-plus' label='Create Folder' />
                                </a>
                                <a href='#' className='link-dark rounded' onClick={this.exportOpml}>
                                    <Icon name='download' label='Export OPML' />
                                </a>
                            </li>
                        </ul>
                    </li>

                    <li className="border-top my-3"></li>

                    {this.showFolders()}

                    <li className="mb-1">
                        <ul className="btn-toggle-nav list-unstyled fw-normal pb-1 small">
                            {this.showFeeds()}
                        </ul>
                    </li>
                </ul>
            </div>
        </>
    }

}

export default withRouter(FeedList);
