import React from 'react';
import { Post } from '../api/Model';
import TimeAgo from '../components/TimeAgo';

interface ListLayoutProps {
    posts: Array<Post>;
}

export default class ListLayout extends React.Component<ListLayoutProps, any> {

    render() {
        const { posts } = this.props;

        return <div className='row'>
            <div className='col'>
                <div className='list-group list-group-flush border-bottom scrollarea'>
                    {posts.map(item => <a key={item.feedPostID} href='#' className='list-group-item list-group-item-action py-3 lh-tight'>
                        <div className='d-flex w-100 align-items-center justify-content-between'>
                            <strong className='mb-1'>{item.title}</strong>
                            <small>
                                <TimeAgo millis={item.updated} />
                            </small>
                        </div>
                        <div className='col-10 mb-1 small'>
                            {item.snippet}
                        </div>
                    </a>)}
                </div>
            </div>
        </div>
    }

}
