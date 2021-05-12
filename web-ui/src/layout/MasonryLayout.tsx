import React from 'react';
import { Masonry } from 'gestalt';
import 'gestalt/dist/gestalt.css';
import { Post } from '../api/Model';
import TimeAgo from '../components/TimeAgo';

class BrickRenderer extends React.Component<any, any> {

    render() {
        const { data: post, itemIdx, isMeasuring } = this.props;

        return <div className="card post-card">
            {this.renderImage(post)}
            <h5 className="card-header">{post.title}</h5>
            <div className="card-body">
                <p className="card-text">{post.snippet}</p>
            </div>
            <div className="card-footer text-muted">
                <small><TimeAgo millis={post.updated} /></small>
            </div>
        </div>
    }

    renderImage(post: Post) {
        if (!post.image || !post.image.url) {
            return;
        }

        const width = post.image.width;
        const height = post.image.height;
        const podWidth = 278;
        const podHeight: number = Math.floor((podWidth * height) / width);

        return <img src={post.image.url} className="card-img-top"
            alt={post.title} width={podWidth} height={podHeight} />
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
