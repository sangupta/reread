import React from 'react';

import ListLayout from '../layout/ListLayout';
import MasonryLayout from '../layout/MasonryLayout';
import MagazineLayout from '../layout/MagazineLayout';
import CardLayout from '../layout/CardLayout';

import { Post } from '../api/Model';
import PostView from './PostView';
import PostApi from '../api/PostApi';

interface ContentPaneProps {
    posts: Array<Post>;
    sort: string;
    include: string;
    layout: string;
}

interface ContentPaneState {
    post: Post;
}

class ContentPane extends React.Component<ContentPaneProps, ContentPaneState> {

    filtered: Array<Post> = [];

    constructor(props: ContentPaneProps) {
        super(props);

        this.state = {
            post: undefined
        }
    }

    showPost = (post: Post) => {
        this.setState({ post: post });
    }

    hidePost = () => {
        this.setState({ post: undefined });
    }

    starPost = async (e: React.MouseEvent) => {
        e.preventDefault();

        const { post } = this.state;
        const updatedPost: Post = await PostApi.toggleStarPost(post.feedPostID, post.starredOn > 0);

        const { posts } = this.props;
        let index = posts.indexOf(post);
        posts[index] = updatedPost;

        this.setState({ post: updatedPost });
    }

    bookmarkPost = async (e: React.MouseEvent) => {
        e.preventDefault();

        const { post } = this.state;
        const updatedPost: Post = await PostApi.toggleBookmarkPost(post.feedPostID, post.bookmarkedOn > 0);

        const { posts } = this.props;
        let index = posts.indexOf(post);
        posts[index] = updatedPost;

        this.setState({ post: updatedPost });
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
            return <PostView key={post.feedPostID}
                post={post}
                onPostHide={this.hidePost}
                onPreviousPost={this.prevPost}
                onToggleStar={this.starPost}
                onToggleBookmark={this.bookmarkPost}
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
                Element = CardLayout;
                break;

            case 'masonry':
                Element = MasonryLayout;
                break;

            case 'magazine':
                Element = MagazineLayout;
                break;

            case 'list':
            default:
                Element = ListLayout;
                break;


        }

        return <div className='content-pane'>
            <Element posts={filtered} onShowPost={this.showPost} />
            {this.renderPost()}
        </div>
    }

}

export default ContentPane;
