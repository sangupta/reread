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
    showLoadMoreButton: boolean;
}

class FeedLoader extends React.Component<FeedLoaderProps, FeedLoaderState> {

    state = {
        loading: true,
        errorMsg: '',
        posts: [],
        sortOption: 'newest',
        includeItems: 'all',
        layout: 'list',
        feedDetails: null,
        showLoadMoreButton:true
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

    handleRefresh = async (e: React.MouseEvent): Promise<void> => {
        e.preventDefault();

        const { mode, match } = this.props;
        const { feedID, folderID } = match?.params;

        switch (mode) {
            case 'feed':
                await FeedApi.refreshFeed(feedID);
                break;

            case 'folder':
                await FeedApi.refreshFolder(folderID);
                break;

            case 'all':
                await FeedApi.refreshAll();
                break;
        }

        // reload data from server
        this.fetchData(mode, feedID, folderID);
    }

    loadMore = async () => {
        const { mode, query: text, match } = this.props;
        const { feedID, folderID } = match?.params;
        const { posts, sortOption, includeItems } = this.state;
        const afterPostID = (posts[posts.length - 1] as Post).feedPostID;

        console.log('posts: ', posts);
        console.log('getting posts after postID: ' + afterPostID);

        const newPosts = await PostApi.getPosts(mode, (text || ''), feedID, folderID, sortOption, includeItems, afterPostID);
        if (newPosts) {
            if (newPosts.length === 0) {
                this.setState({ showLoadMoreButton: false });
                return;

            }
            this.setState({ posts: [...posts, ...newPosts] });
        }
    }

    fetchData = async (mode: string, feedID: string, folderID: string, afterPostID: string = '') => {
        let data;

        this.setState({ loading: true, errorMsg: '' });

        const { sortOption, includeItems } = this.state;
        const text = this.props.query || '';

        const posts = await PostApi.getPosts(mode, text, feedID, folderID, sortOption, includeItems, afterPostID);
        this.setState({ posts: posts, loading: false });
    }

    markAllAsHandler = async (value: string) => {
        const { posts } = this.state;

        const updatedPosts = await PostApi.toggleMarking(posts, value);
        this.setState({ posts: updatedPosts });
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
        const { loading, errorMsg, posts, layout, showLoadMoreButton } = this.state;

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

        return <ContentPane posts={posts} layout={layout} onLoadMore={this.loadMore} showLoadMoreButton={showLoadMoreButton} />
    }

}

export default withRouter(FeedLoader);
