import React from 'react';
import { Post } from './../api/Model';
import PostApi from '../api/PostApi';
import TimeAgo from '../components/TimeAgo';
import FeedApi from '../api/FeedApi';
import Icon from '../components/Icon';

interface PostViewProps {
    post: Post;
    onPostHide: () => void;
    onPreviousPost: React.MouseEventHandler;
    onNextPost: React.MouseEventHandler;
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

    hidePost = () => {
        this.props.onPostHide();
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

        return <div className='modal-layer'>
            <div className='modal-underlay' />
            <div className='modal d-block' tabIndex={-1} role='dialog'>
                <div className='modal-dialog post-view-modal' role='document'>
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
                            <button type="button" className="btn-close" aria-label="Close" onClick={this.hidePost} />
                        </div>
                        <div className='modal-toolbar'>
                            <a href='#'>
                                <Icon name='star' className='pe-2' />
                            </a>
                            <a href='#'>
                                <Icon name='bookmark' className='pe-2' />
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
                </div>
            </div>
        </div >
    }

}
