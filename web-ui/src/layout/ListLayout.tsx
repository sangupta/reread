import React from 'react';
import { Post } from '../api/Model';

import TimeAgo from '../components/TimeAgo';

interface ListLayoutItemProps {
    post: Post;
    onShowPost: Function;
}

interface ListLayoutItemState {
    postRead: boolean;
}

class ListLayoutItem extends React.Component<ListLayoutItemProps, ListLayoutItemState> {

    constructor(props: ListLayoutItemProps) {
        super(props);
        this.state = {
            postRead: props.post ? props.post.readOn > 0 : false
        }
    }

    componentDidUpdate(prevProps: any) {
        if (this.props.post === prevProps.post) {
            return;
        }
        const { postRead } = this.state;
        const readOn = this.props.post.readOn > 0;
        if (postRead !== readOn) {
            this.setState({ postRead: readOn });
        }
    }

    showPost = (e: React.MouseEvent) => {
        e.preventDefault();
        this.setState({ postRead: true });
        this.props.onShowPost(this.props.post);
    }

    render() {
        const { post } = this.props;
        const { postRead } = this.state;
        const css: string = postRead ? ' list-group-item-secondary' : '';

        return <a key={post.feedPostID} href='#' className={'list-group-item list-group-item-action py-3 lh-tight ' + css} onClick={this.showPost}>
            <div className='d-flex w-100 align-items-center justify-content-between'>
                <strong className='mb-1'>{post.title}</strong>
                <small>
                    <TimeAgo millis={post.updated} />
                </small>
            </div>
            <div className='col-10 mb-1 small'>
                {post.snippet}
            </div>
        </a>
    }
}

interface ListLayoutProps {
    posts: Array<Post>;
    onShowPost: () => void;
}

export default class ListLayout extends React.Component<ListLayoutProps> {

    render() {
        const { posts } = this.props;

        return <div className='list-group list-group-flush border-bottom scrollarea'>
            {posts.map(item =>
                <ListLayoutItem key={item.feedPostID}
                    post={item}
                    onShowPost={this.props.onShowPost} />
            )}
        </div>
    }

}
