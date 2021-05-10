import React from 'react';
import { Post } from './../api/Model';
import PostApi from '../api/PostApi';

interface PostViewProps {
    post: Post;
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
                            <h5 className="modal-title">{post.title}</h5>
                            <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div className='modal-content h-100'>
                            <div className='post-details' dangerouslySetInnerHTML={{ __html: post.content }} />
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            <button type="button" className="btn btn-primary">Save changes</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    }

}
