import React from 'react';
import { Masonry } from 'gestalt';
import 'gestalt/dist/gestalt.css';
import { Post } from '../api/Model';
import TimeAgo from '../components/TimeAgo';

class BrickRenderer extends React.Component<any, any> {

    showPost = () => {
        const { data: post } = this.props;

        const event = new CustomEvent('reread-show-post', {
            detail: {
                post: post
            }
        });
        document.dispatchEvent(event);
    }

    render() {
        const { data: post } = this.props;
        let hasImage = false;
        if (post.image && post.image.url) {
            hasImage = true;
        }

        return <div className="card post-card pointer" onClick={this.showPost}>
            {this.renderImage(post)}
            {!hasImage && <h5 className="card-header">{post.title}</h5>}
            <div className="card-body">
                {hasImage && <h5 className="card-title">{post.title}</h5>}
                <p className="card-text">{post.snippet}</p>
            </div>
            <div className="card-footer text-muted">
                <small><TimeAgo millis={post.updated} /></small>
            </div>
        </div>
    }

    renderImage(post: Post) {
        if (!post.image || !post.image.url) {
            return null;
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

    getContentPane(): HTMLElement {
        return document.querySelector('.content-pane') as HTMLElement;
    }

    render() {
        const { posts } = this.props;

        return <Masonry comp={BrickRenderer}
            items={posts}
            virtualize={true}
            scrollContainer={this.getContentPane}
            virtualBoundsBottom={1000}
            virtualBoundsTop={200}
            columnWidth={280}
            gutterWidth={20}
            minCols={1} />
    }

}
