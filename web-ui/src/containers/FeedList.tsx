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
}

class FeedList extends React.Component<FeedListProps, FeedListState> {

    state: FeedListState = {
        feeds: [],
        folders: [],
        loading: true
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
            {folders.map(folder => <FolderItems key={folder.folderID} folder={folder} />)}
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

    showAllFeeds = () => {
        this.props.history.push('/feeds/all')
    }

    showStars = () => {
        this.props.history.push('/feeds/stars')
    }

    showBookmarks = () => {
        this.props.history.push('/feeds/bookmarks')
    }

    createFolder = async () => {
        const folderName = window.prompt('Enter the name of the folder: ', '');
        if (!folderName) {
            return;
        }

        const data = await FeedApi.createFolder(folderName);
        this.fetchList();
    }

    render() {
        const { loading } = this.state;
        if (loading) {
            return <Loading />
        }

        return <>
            <div className="bg-white feed-list">
                <ul className="list-unstyled ps-0">
                    <li className="mb-1">
                        <ul className="btn-toggle-nav list-unstyled fw-normal pb-1 small">
                            <li>
                                <a href='#' className='link-dark rounded' onClick={this.showAllFeeds}>
                                    <Icon name='basket' label='All' />
                                </a>
                            </li>
                            <li>
                                <a href='#' className='link-dark rounded' onClick={this.showStars}>
                                    <Icon name='star' label='Stars' />
                                </a>
                            </li>
                            <li>
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
