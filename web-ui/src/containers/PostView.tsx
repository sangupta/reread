import React from 'react';

import FeedApi from '../api/FeedApi';
import PostApi from '../api/PostApi';
import { Post } from './../api/Model';

import TimeAgo from '../components/TimeAgo';
import Icon from '../components/Icon';
import Modal from '../components/Modal';

interface PostViewProps {
    post: Post;
    onPostHide: () => void;
    onPreviousPost: React.MouseEventHandler;
    onNextPost: React.MouseEventHandler;
    onToggleStar: (e: React.MouseEvent) => void;
    onToggleBookmark: (e: React.MouseEvent) => void;
}

export default class PostView extends React.Component<PostViewProps> {

    componentDidMount() {
        document.body.classList.add('modal-open');

        const { post } = this.props;
        PostApi.markRead(post.feedPostID);
    }

    componentWillUnmount() {
        document.body.classList.remove('modal-open');
    }

    showPostAuthor = () => {
        const { post } = this.props;
        const name = post.author?.name;
        const uri = post.author?.uri || '#';

        if (name) {
            return <span className='post-author'>by <a href={uri}>{name}</a></span>
        }
    }

    showPostFeed = () => {
        const { post } = this.props;
        const feed = FeedApi.getFeedDetails(post.masterFeedID);
        return <span className='post-feed-name'>from <a href='#'>{feed ? feed.title : post.masterFeedID}</a></span>
    }

    render() {
        const { post } = this.props;
        if (!post) {
            return null;
        }

        const starIcon = post.starredOn > 0 ? 'star-fill' : 'star';
        const bookmarkIcon = post.bookmarkedOn > 0 ? 'bookmark-check-fill' : 'bookmark';

        return <Modal className='post-view-modal' onCloseModal={this.props.onPostHide}>
            <div className="modal-content post-view-content">
                <div className="modal-header">
                    <div className='d-flex flex-column'>
                        <h5 className="modal-title">{post.title}</h5>
                        <div className='d-flex flex-row post-subtitle'>
                            {this.showPostAuthor()}
                            <span className='post-time'>posted <TimeAgo millis={post.updated} /></span>
                            {this.showPostFeed()}
                        </div>
                    </div>
                    <button type="button" className="btn-close" aria-label="Close" onClick={this.props.onPostHide} />
                </div>
                <div className='modal-toolbar'>
                    <a href='#' onClick={this.props.onToggleStar}>
                        <Icon name={starIcon} className='pe-2' />
                    </a>
                    <a href='#' onClick={this.props.onToggleBookmark}>
                        <Icon name={bookmarkIcon} className='pe-2' />
                    </a>
                    <a href='#'>
                        <Icon name='check-circle-fill' />
                    </a>
                    <div className='v-spacer' />
                    <a href='#' onClick={this.props.onPreviousPost}>
                        <Icon name='arrow-left-circle' className='pe-2' />
                    </a>
                    <a href='#' onClick={this.props.onNextPost}>
                        <Icon name='arrow-right-circle' />
                    </a>
                    <div className='v-spacer' />
                    <a href={post.link || '#'} target='_blank'>
                        <Icon name='link-45deg' className='pe-2' />
                    </a>
                </div>
                <div className='modal-body h-100'>
                    <div className='post-details' dangerouslySetInnerHTML={{ __html: post.content }} />
                </div>
            </div>
        </Modal>
    }

}
