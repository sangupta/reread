import React from 'react';
import { collect, WithStoreProp } from 'react-recollect';
import ListLayout from '../layout/ListLayout';
import { Post } from '../api/Model';
import PostView from './PostView';

interface ContentPaneProps extends WithStoreProp {
    posts: Array<Post>;
}

interface ContentPaneState {
    post: Post | null;
}

class ContentPane extends React.Component<ContentPaneProps, ContentPaneState> {

    state = {
        post: null
    }

    showPost = (post: Post) => {
        if (!post) {
            return;
        }

        this.setState({ post: post });
    }

    hidePost = () => {
        this.setState({ post: null });
    }

    nextPost = (e:React.MouseEvent) => {
        e.preventDefault();

        const { posts } = this.props;
        const { post } = this.state;

        let index = posts.indexOf(post);
        index = index + 1;
        if(index >= posts.length) {
            return;
        }

        this.setState({ post: posts[index] });
    }

    prevPost = (e:React.MouseEvent) => {
        e.preventDefault();
        
        const { posts } = this.props;
        const { post } = this.state;

        let index = posts.indexOf(post);
        index = index - 1;
        if (index < 0) {
            return;
        }

        this.setState({ post: posts[index] });
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
        const { posts, store } = this.props;
        const layout = store.layout;

        let Element;
        switch (layout) {
            case 'cards':
            case 'list':
                Element = ListLayout;
        }

        return <div className='content-pane'>
            <Element posts={posts} onShowPost={this.showPost} onPostHide={this.hidePost} />
            {this.renderPost()}
        </div>
    }

}

export default collect(ContentPane);
