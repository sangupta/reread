import React from 'react';
import { Masonry } from 'gestalt';
import 'gestalt/dist/gestalt.css';
import { Post } from '../api/Model';

class CardRenderer extends React.Component<any, any> {

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
            {/* <img src="http://i.imgur.com/PlMvMqN.jpg" className="card-img-top" alt="..." width='278px' height='165px' /> */}
            <div className="card-body">
                <h5 className="card-title">{post.title}</h5>
                <p className="card-text">{post.snippet}</p>
                <a href="#" className="btn btn-primary">Go somewhere</a>
            </div>
        </div>
    }
}

export default class CardLayout extends React.Component<any, any> {

    getContentPane() {
        return document.querySelector('.content-pane');
    }

    render() {
        const { posts } = this.props;

        return <Masonry comp={CardRenderer}
            items={posts}
            virtualize={true} scrollContainer={this.getContentPane}
            virtualBoundsBottom={1000}
            virtualBoundsTop={200}
            columnWidth={280}
            gutterWidth={20}
            minCols={1} />
    }

}
