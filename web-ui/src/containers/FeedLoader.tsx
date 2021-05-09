import React from 'react';
import { withRouter } from 'react-router-dom';

import Loading from '../components/Loading';
import Alert from '../components/Alert';
import TimeLineApi from '../api/TimeLineApi';
import ContentPane from './ContentPane';
import { Post } from '../api/Model';

interface FeedLoaderProps {
    mode: 'feed' | 'folder' | 'all';
    match: any;
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
        const { mode, match } = this.props;
        const { feedID, folderID } = match?.params;

        const { mode: oldMode, match: oldMatch } = prevProps;
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

        if (refetch) {
            this.fetchData(mode, feedID, folderID);
        }
    }

    fetchData = async (mode: string, feedID: string, folderID: string) => {
        let data;

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

        this.setState({ posts: data, loading: false });
    }

    render() {
        const { loading, errorMsg, posts } = this.state;
        if (loading) {
            return <Loading />
        }

        if (errorMsg) {
            return <Alert level='error'>{errorMsg}</Alert>
        }

        if (posts.length === 0) {
            return <Alert>Feed has no posts.</Alert>
        }

        return <ContentPane posts={posts} />
    }

}

export default withRouter(FeedLoader);
