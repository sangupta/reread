import React from 'react';

import TimeAgo from '../components/TimeAgo';
import { Post } from '../api/Model';

interface CardItemRendererProps {
    post: Post;
    onShowPost: Function;
}

interface CardItemRendererState {
    postRead: boolean;
}

class CardItemRenderer extends React.Component<CardItemRendererProps, CardItemRendererState> {

    constructor(props: CardItemRendererProps) {
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
        return <div className='card fixed-card pointer' onClick={this.showPost}>
            <div className="card-body">
                <h5 className="card-title">{post.title}</h5>
                <p className="card-text fixed-snippet">{post.snippet}</p>
            </div>
            <div className="card-footer text-muted">
                <small><TimeAgo millis={post.updated} /></small>
            </div>
        </div>
    }
}

export default class CardLayout extends React.Component<any, any> {

    render() {
        const { posts } = this.props;

        return <div className='card-layout-container d-flex flex-wrap'>
            {posts.map(item => <CardItemRenderer 
                                    key={item.feedPostID} 
                                    onShowPost={this.props.onShowPost}
                                    post={item} />)}
        </div>
    }
}
