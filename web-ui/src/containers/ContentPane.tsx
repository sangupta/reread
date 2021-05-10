import React from 'react';

import ListLayout from '../layout/ListLayout';
import { Post } from '../api/Model';
import PostView from './PostView';
import PostApi from '../api/PostApi';

interface ContentPaneProps extends WithStoreProp {
    posts: Array<Post>;
    sort: string;
    include: string;
    layout: string;
}

interface ContentPaneState {
    post: Post | null;
}

class ContentPane extends React.Component<ContentPaneProps, ContentPaneState> {

    filtered: Array<Post> = [];

    constructor(props: ContentPaneProps) {
        super(props);

        this.state = {
            post: null
        }
    }

    showPost = (post: Post) => {
        this.setState({ post: post });
    }

    hidePost = () => {
        this.setState({ post: null });
    }

    onStarPost = async (e: React.MouseEvent) => {
        e.preventDefault();

        const { post } = this.state;
        const updatedPost: Post = await PostApi.starPost(post.feedPostID);

        const { posts } = this.props;
        let index = posts.indexOf(post);
        posts[index].starredOn = updatedPost.starredOn;
    }

    nextPost = (e: React.MouseEvent) => {
        e.preventDefault();

        const { post } = this.state;

        let index = this.filtered.indexOf(post);
        index = index + 1;
        if (index >= this.filtered.length) {
            return;
        }

        this.setState({ post: this.filtered[index] });
    }

    prevPost = (e: React.MouseEvent) => {
        e.preventDefault();

        const { post } = this.state;

        let index = this.filtered.indexOf(post);
        index = index - 1;
        if (index < 0) {
            return;
        }

        this.setState({ post: this.filtered[index] });
    }

    renderPost = () => {
        const { post } = this.state;
        if (post) {
            return <PostView key={post.feedPostID} post={post}
                onPostHide={this.hidePost}
                onPreviousPost={this.prevPost}
                onNextPost={this.nextPost} />
        }
    }

    render() {
        const { posts, layout, sort, include } = this.props;

        let filtered = [...posts];
        if (sort === 'newest') {
            filtered.sort((a: Post, b: Post) => {
                if (a.updated > b.updated) {
                    return -1;
                }

                return 1;
            });
        } else {
            filtered.sort((a: Post, b: Post) => {
                if (a.updated > b.updated) {
                    return 1;
                }

                return -1;
            });
        }

        if (include === 'read') {
            filtered = filtered.filter(post => post.readOn > 0);
        } else if (include === 'unread') {
            filtered = filtered.filter(post => post.readOn === 0);
        }

        this.filtered = filtered;

        let Element;
        switch (layout) {
            case 'cards':
            case 'list':
                Element = ListLayout;
        }

        return <div className='content-pane'>
            <Element posts={filtered} onShowPost={this.showPost} onPostHide={this.hidePost} />
            {this.renderPost()}
        </div>
    }

}

export default ContentPane;
