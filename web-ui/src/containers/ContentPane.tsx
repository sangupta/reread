import React from 'react';

import ListLayout from '../layout/ListLayout';
import MasonryLayout from '../layout/MasonryLayout';
import MagazineLayout from '../layout/MagazineLayout';
import CardLayout from '../layout/CardLayout';

import { Post } from '../api/Model';
import PostView from './PostView';
import PostApi from '../api/PostApi';
import TitleOnlyLayout from '../layout/TitleOnly';

interface ContentPaneProps {
    posts: Array<Post>;
    layout: string;
    onLoadMore: () => void;
    showLoadMoreButton: boolean;
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

    starPost = async (e: React.MouseEvent) => {
        e.preventDefault();

        const { post } = this.state;
        if (post == null) {
            return;
        }

        const updatedPost: Post = await PostApi.toggleStarPost(post.feedPostID, post.starredOn > 0);

        const { posts } = this.props;
        let index = posts.indexOf(post);
        posts[index] = updatedPost;

        this.setState({ post: updatedPost });
    }

    bookmarkPost = async (e: React.MouseEvent) => {
        e.preventDefault();

        const { post } = this.state;
        if (post == null) {
            return;
        }

        const updatedPost: Post = await PostApi.toggleBookmarkPost(post.feedPostID, post.bookmarkedOn > 0);

        const { posts } = this.props;
        let index = posts.indexOf(post);
        posts[index] = updatedPost;

        this.setState({ post: updatedPost });
    }

    nextPost = (e: React.MouseEvent) => {
        e.preventDefault();

        const { post } = this.state;
        if (post == null) {
            return;
        }

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
        if (post == null) {
            return;
        }
        
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
            return <PostView key={post.feedPostID}
                post={post}
                onPostHide={this.hidePost}
                onPreviousPost={this.prevPost}
                onToggleStar={this.starPost}
                onToggleBookmark={this.bookmarkPost}
                onNextPost={this.nextPost} />
        }
    }

    showLoadMoreButton = () => {
        if (!this.props.showLoadMoreButton) {
            return null;
        }

        return <div className='content-pane-more'>
            <button type='button' className='btn btn-sm btn-primary' onClick={this.props.onLoadMore}>Load More</button>
        </div>
    }

    render() {
        const { posts, layout } = this.props;

        let Element;
        switch (layout) {

            case 'cards':
                Element = CardLayout;
                break;

            case 'masonry':
                Element = MasonryLayout;
                break;

            case 'magazine':
                Element = MagazineLayout;
                break;

            case 'title':
                Element = TitleOnlyLayout;
                break;

                case 'list':
            default:
                Element = ListLayout;
                break;
        }

        return <div className='content-pane'>
            <Element posts={posts} onShowPost={this.showPost} />
            {this.showLoadMoreButton()}

            {this.renderPost()}
        </div>
    }

}

export default ContentPane;
