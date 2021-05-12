import React from 'react';
import { Masonry } from 'gestalt';
import 'gestalt/dist/gestalt.css';
import { Post } from '../api/Model';
import TimeAgo from '../components/TimeAgo';

class BrickRenderer extends React.Component<any, any> {

    render() {
        return this.renderMasonry();
    }

    renderStack = () => {
        const { post } = this.props;
        return this.renderContent(post);
    }

    renderMasonry = () => {
        const { data: post, itemIdx, isMeasuring } = this.props;
        return this.renderContent(post);
    }

    renderContent = (post: Post) => {
        return <div className="card post-card">
            <h5 className="card-header">{post.title}</h5>
            <div className="card-body">
                <p className="card-text">{post.snippet}</p>
                <a href="#" className="btn btn-primary">Go somewhere</a>
            </div>
            <div className="card-footer text-muted">
                <small><TimeAgo millis={post.updated} /></small>
            </div>
        </div>
    }
}

export default class MasonryLayout extends React.Component<any, any> {

    getContentPane() {
        return document.querySelector('.content-pane');
    }

    render() {
        const { posts } = this.props;

        return <Masonry comp={BrickRenderer}
            items={posts}
            virtualize={true} scrollContainer={this.getContentPane}
            virtualBoundsBottom={1000}
            virtualBoundsTop={200}
            columnWidth={280}
            gutterWidth={20}
            minCols={1} />
    }

}
