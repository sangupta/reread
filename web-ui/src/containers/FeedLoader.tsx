import React from 'react';
import { withRouter } from 'react-router-dom';

import Loading from '../components/Loading';
import Alert from '../components/Alert';
import TimeLineApi from '../api/TimeLineApi';
import ContentPane from './ContentPane';
import { Post } from '../api/Model';
import PostApi from '../api/PostApi';

interface FeedLoaderProps {
    mode: 'feed' | 'folder' | 'all' | 'search';
    match: any;
    query?: string;
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
        const { mode, match, query } = this.props;
        const { feedID, folderID } = match?.params;

        const { mode: oldMode, match: oldMatch, query: oldQuery } = prevProps;
        const { feedID: oldFeedID, folderID: oldFolderID } = oldMatch?.params;

        let refetch = false;
        if (mode !== oldMode) {
            refetch = true;
        }

        if (mode === 'feed' && feedID !== oldFeedID) {
            refetch = true;
        }

        if (mode === 'folder' && folderID !== oldFolderID) {
            refetch = true;
        }

        if (mode === 'search' && query !== oldQuery) {
            refetch = true;
        }

        if (refetch) {
            this.fetchData(mode, feedID, folderID);
        }
    }

    fetchData = async (mode: string, feedID: string, folderID: string) => {
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
            data = await TimeLineApi.getTimeLine();
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

        this.setState({ posts: data, loading: false, errorMsg: '' });
    }

    render() {
        const { query, mode } = this.props;
        const { loading, errorMsg, posts } = this.state;

        if (loading) {
            return <Loading />
        }

        if (errorMsg) {
            return <Alert level='error'>{errorMsg}</Alert>
        }

        if (posts.length === 0) {
            if (mode === 'search') {
                return <Alert>No results found for the query: {query}</Alert>
            }

            return <Alert>Feed has no posts.</Alert>
        }

        return <ContentPane posts={posts} />
    }

}

export default withRouter(FeedLoader);
