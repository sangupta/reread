import React from 'react';
import { Post } from '../api/Model';

import TimeAgo from '../components/TimeAgo';

interface TitleOnlyLayoutItemProps {
    post: Post;
    onShowPost: Function;
}

interface TitleOnlyLayoutItemState {
    postRead: boolean;
}

class TitleOnlyLayoutItem extends React.Component<TitleOnlyLayoutItemProps, TitleOnlyLayoutItemState> {

    constructor(props: TitleOnlyLayoutItemProps) {
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

        return <a key={post.feedPostID} href='#' className={'list-group-item list-group-item-action lh-tight ' + css} onClick={this.showPost}>
            <div className='d-flex w-100 align-items-center justify-content-between'>
                <strong className='mb-1'>{post.title}</strong>
                <small>
                    <TimeAgo millis={post.updated} />
                </small>
            </div>
        </a>
    }
}

interface TitleOnlyLayoutProps {
    posts: Array<Post>;
    onShowPost: () => void;
}

export default class TitleOnlyLayout extends React.Component<TitleOnlyLayoutProps> {

    render() {
        const { posts } = this.props;

        return <div className='list-group list-group-flush border-bottom scrollarea'>
            {posts.map(item =>
                <TitleOnlyLayoutItem key={item.feedPostID}
                    post={item}
                    onShowPost={this.props.onShowPost} />
            )}
        </div>
    }

}
