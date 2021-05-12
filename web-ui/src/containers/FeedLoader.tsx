import React from 'react';
import { withRouter } from 'react-router-dom';

import Loading from '../components/Loading';
import Alert from '../components/Alert';
import TimeLineApi from '../api/TimeLineApi';
import ContentPane from './ContentPane';
import { Post } from '../api/Model';
import PostApi from '../api/PostApi';
import Toolbar from './Toolbar';
import FeedApi from '../api/FeedApi';
import FeedDetailsContainer from './FeedDetailsContainer';

interface FeedLoaderProps {
    mode: 'feed' | 'folder' | 'all' | 'search' | 'stars' | 'bookmarks';
    match: any;
    query?: string;
}

interface FeedLoaderState {
    loading: boolean;
    errorMsg: string;
    posts: Array<Post>;
    sortOption: string;
    includeItems: string;
    layout: string;
    feedDetails: any;
}

class FeedLoader extends React.Component<FeedLoaderProps, FeedLoaderState> {

    state = {
        loading: true,
        errorMsg: '',
        posts: [],
        sortOption: 'newest',
        includeItems: 'all',
        layout: 'list',
        feedDetails: null
    }

    componentDidMount() {
        const { mode, match } = this.props;
        const { feedID, folderID } = match?.params;

        this.fetchData(mode, feedID, folderID);
    }

    componentDidUpdate(prevProps: any) {
        const { mode, match, query } = this.props;
        const { feedID, folderID } = match?.params;

        const { mode: oldMode, match: oldMatch, query: oldQuery } = prevProps;
        const { feedID: oldFeedID, folderID: oldFolderID } = oldMatch?.params;

        let refetch = false;
        refetch = (mode !== oldMode);
        refetch = refetch || (mode === 'feed' && feedID !== oldFeedID);
        refetch = refetch || (mode === 'folder' && folderID !== oldFolderID)
        refetch = refetch || (mode === 'search' && query !== oldQuery)

        if (refetch) {
            this.fetchData(mode, feedID, folderID);
        }
    }

    handleRefresh = (e: React.MouseEvent): void => {
        e.preventDefault();

        const { mode, match } = this.props;
        const { feedID, folderID } = match?.params;

        this.fetchData(mode, feedID, folderID);
    }

    fetchData = async (mode: string, feedID: string, folderID: string) => {
        let data;

        this.setState({ loading: true, errorMsg: '' });

        if (mode === 'search') {
            const text = this.props.query;
            if (!text) {
                this.setState({ loading: false, errorMsg: 'Query text to search not found' });
                return;
            }

            data = await PostApi.search(text);
        }

        if (mode === 'all') {
            data = await TimeLineApi.getTimeLine();
        }

        if (mode === 'stars') {
            data = await TimeLineApi.getStarsTimeLine();
        }

        if (mode === 'bookmarks') {
            data = await TimeLineApi.getBookmarksTimeLine();
        }

        if (mode === 'feed') {
            if (!feedID) {
                this.setState({ loading: false, errorMsg: 'No such feed found' });
                return;
            }

            data = await TimeLineApi.getFeedTimeLine(feedID);
        }
        if (mode === 'folder') {
            if (!folderID) {
                this.setState({ loading: false, errorMsg: 'No such folder found' });
                return;
            }

            data = await TimeLineApi.getFolderTimeLine(folderID);
        }

        this.setState({ posts: data, loading: false });
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

    sortChange = (v: string): void => {
        this.setState({ sortOption: v });
    }

    includeChange = (v: string): void => {
        this.setState({ includeItems: v });
    }

    layoutChange = (v: string): void => {
        this.setState({ layout: v });
    }

    showFeedDetails = async (e: React.MouseEvent) => {
        e.preventDefault();
        const { match } = this.props;
        const { feedID } = match?.params;
        const data = await FeedApi.getFeedCrawlDetails(feedID);
        this.setState({ feedDetails: data });
    }

    render() {
        const { sortOption, includeItems, layout } = this.state;
        const { mode } = this.props;
        const singleFeed = mode === 'feed';

        return <div className='d-flex flex-column'>
            <Toolbar onMarkAllAs={this.markAllAsHandler}
                sortOption={sortOption}
                includeItems={includeItems}
                layout={layout}
                showFeedDetails={singleFeed}
                onSortChange={this.sortChange}
                onIncludeChange={this.includeChange}
                onFeedDetails={this.showFeedDetails}
                onLayoutChange={this.layoutChange}
                onRefresh={this.handleRefresh} />
            {this.renderContent()}
            {this.renderFeedDetailsModal()}
        </div>
    }

    closeDetailsModal = (e: React.MouseEvent): void => {
        this.setState({ feedDetails: null });
    }

    renderFeedDetailsModal = () => {
        const { feedDetails } = this.state;
        if (!feedDetails) {
            return null;
        }

        return <FeedDetailsContainer details={feedDetails} onModalClose={this.closeDetailsModal} />
    }

    renderContent() {
        const { query, mode } = this.props;
        const { loading, errorMsg, posts, sortOption, includeItems, layout } = this.state;

        if (loading) {
            return <Loading />
        }

        if (errorMsg) {
            return <Alert className='mx-3' level='error'>{errorMsg}</Alert>
        }

        if (!posts || posts.length === 0) {
            if (mode === 'search') {
                return <Alert className='mx-3'>No results found for the query: {query}</Alert>
            }

            return <Alert className='mx-3'>Feed has no posts.</Alert>
        }

        return <ContentPane posts={posts} sort={sortOption} include={includeItems} layout={layout} />
    }

}

export default withRouter(FeedLoader);
