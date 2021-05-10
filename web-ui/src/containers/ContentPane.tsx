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

    renderPost = () => {
        const { post } = this.state;
        if (post) {
            return <PostView post={post} onPostHide={this.hidePost} />
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
