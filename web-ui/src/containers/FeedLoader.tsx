import React from 'react';
import { withRouter } from 'react-router-dom';

import Loading from '../components/Loading';
import Alert from '../components/Alert';
import TimeLineApi from '../api/TimeLineApi';
import ContentPane from './ContentPane';
import { Post } from '../api/Model';
import PostApi from '../api/PostApi';
import Toolbar from './Toolbar';

interface FeedLoaderProps {
    mode: 'feed' | 'folder' | 'all' | 'search' | 'stars' | 'bookmarks';
    match: any;
    query?: string;
    displayItem?: string;
    sortOption?: string;
}

interface FeedLoaderState {
    loading: boolean;
    errorMsg: string;
    posts: Array<Post>;
}

class FeedLoader extends React.Component<FeedLoaderProps, FeedLoaderState> {

    state = {
        loading: true,
        errorMsg: '',
        posts: []
    }

    componentDidMount() {
        const { mode, match } = this.props;
        const { feedID, folderID } = match?.params;

        this.fetchData(mode, feedID, folderID);
    }

    componentDidUpdate(prevProps: any) {
        const { mode, match, query, sortOption, displayItem } = this.props;
        const { feedID, folderID } = match?.params;

        const { mode: oldMode, match: oldMatch, query: oldQuery, sortOption: oldSortOption, displayItem: oldDisplayItem } = prevProps;
        const { feedID: oldFeedID, folderID: oldFolderID } = oldMatch?.params;

        let refetch = false;
        refetch = (mode !== oldMode);
        refetch = refetch || (sortOption != oldSortOption);
        refetch = refetch || (displayItem != oldDisplayItem);
        refetch = refetch || (mode === 'feed' && feedID !== oldFeedID);
        refetch = refetch || (mode === 'folder' && folderID !== oldFolderID)
        refetch = refetch || (mode === 'search' && query !== oldQuery)

        if (refetch) {
            this.fetchData(mode, feedID, folderID, sortOption, displayItem);
        }
    }

    fetchData = async (mode: string, feedID: string, folderID: string, sort: string = '', include: string = '') => {
        let data;

        if (mode === 'search') {
            const text = this.props.query;
            if (!text) {
                this.setState({ loading: false, errorMsg: 'Query text to search not found' });
                return;
            }

            data = await PostApi.search(text);
        }

        if (mode === 'all') {
            data = await TimeLineApi.getTimeLine(sort, include);
        }

        if (mode === 'stars') {
            data = await TimeLineApi.getStarsTimeLine(sort, include);
        }

        if (mode === 'bookmarks') {
            data = await TimeLineApi.getBookmarksTimeLine(sort, include);
        }

        if (mode === 'feed') {
            if (!feedID) {
                this.setState({ loading: false, errorMsg: 'No such feed found' });
                return;
            }

            data = await TimeLineApi.getFeedTimeLine(feedID, sort, include);
        }
        if (mode === 'folder') {
            if (!folderID) {
                this.setState({ loading: false, errorMsg: 'No such folder found' });
                return;
            }

            data = await TimeLineApi.getFolderTimeLine(folderID, sort, include);
        }

        this.setState({ posts: data, loading: false, errorMsg: '' });
    }

    markAllAsHandler = async (value: string) => {
        const { posts } = this.state;

        const ids: Array<string> = [];
        posts.forEach(post => {
            ids.push((post as Post).feedPostID);
        });

        if ('markRead' === value) {
            const posts = await PostApi.markAllRead(ids);
            this.setState({ posts: posts });
            return;
        }

        if ('markUnread' === value) {
            const posts = await PostApi.markAllUnread(ids);
            this.setState({ posts: posts });
            return;
        }
    }

    renderContent() {
        const { query, mode } = this.props;
        const { loading, errorMsg, posts } = this.state;

        if (loading) {
            return <Loading />
        }

        if (errorMsg) {
            return <Alert level='error'>{errorMsg}</Alert>
        }

        if (!posts || posts.length === 0) {
            if (mode === 'search') {
                return <Alert>No results found for the query: {query}</Alert>
            }

            return <Alert>Feed has no posts.</Alert>
        }

        return <ContentPane posts={posts} />
    }

    render() {
        return <div className='d-flex flex-column'>
            <Toolbar onMarkAllAs={this.markAllAsHandler} />
            {this.renderContent()}
        </div>
    }

}

export default withRouter(FeedLoader);
