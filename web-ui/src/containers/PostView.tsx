import React from 'react';
import { Post } from './../api/Model';

interface PostViewProps {
    post: Post;
}

export default class PostView extends React.Component<PostViewProps> {

    componentDidMount() {
        document.body.classList.add('modal-open');
    }

    componentWillUnmount() {
        document.body.classList.remove('modal-open');
    }

    render() {
        const { post } = this.props;

        return <div className='modal-layer'>
            <div className='modal-underlay' />
            <div className='modal fade show d-block' role='dialog'>
                <div className='modal-dialog' role='document'>
                    <div className='modal-content'>
                        {post.content}
                    </div>
                </div>
            </div>
        </div>
    }

}
