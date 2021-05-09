import React from 'react';
import { Post } from '../api/Model';

import TimeAgo from '../components/TimeAgo';
import PostView from '../containers/PostView';

interface ListLayoutItemProps {
    post: Post;
}

interface ListLayoutItemState {
    open: boolean;
}

class ListLayoutItem extends React.Component<ListLayoutItemProps, ListLayoutItemState> {

    state = {
        open: false
    }

    toggleContent = (e: React.MouseEvent) => {
        e.preventDefault();
        this.setState({ open: !this.state.open });
    }

    showContents = () => {
        const { open } = this.state;
        const { post } = this.props;
        if (!open) {
            return null;
        }

        return <PostView post={post} />
    }

    render() {
        const { post } = this.props;
        return <a key={post.feedPostID} href='#' className='list-group-item list-group-item-action py-3 lh-tight' onClick={this.toggleContent}>
            <div className='d-flex w-100 align-items-center justify-content-between'>
                <strong className='mb-1'>{post.title}</strong>
                <small>
                    <TimeAgo millis={post.updated} />
                </small>
            </div>
            <div className='col-10 mb-1 small'>
                {post.snippet}
            </div>
            {this.showContents()}
        </a>
    }
}

interface ListLayoutProps {
    posts: Array<Post>;
}

export default class ListLayout extends React.Component<ListLayoutProps, any> {

    render() {
        const { posts } = this.props;

        return <div className='row'>
            <div className='col'>
                <div className='list-group list-group-flush border-bottom scrollarea'>
                    {posts.map(item => <ListLayoutItem key={item.feedPostID} post={item} />)}
                </div>
            </div>
        </div>
    }

}
