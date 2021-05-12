import React from 'react';
import TimeAgo from '../components/TimeAgo';

class CardItemRenderer extends React.Component {

    render() {
        const { post } = this.props;
        return <div className='card fixed-card'>
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
            {posts.map(item => <CardItemRenderer post={item} />)}
        </div>
    }
}
