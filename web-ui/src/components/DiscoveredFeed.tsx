import React from 'react';
import { Feed } from '../api/Model';

interface DiscoveredFeedProps {
    feed: Feed;
}

export default class DiscoveredFeed extends React.Component<DiscoveredFeedProps> {

    addFeed = () => {
        
    }

    render() {
        const { feed } = this.props;

        return <div className='row'>
            <div className='col-auto mx-1'>&nbsp;</div>
            <div className='col-auto'>{feed.title}</div>
            <div className='col-auto'>
                <button className='btn btn-sm btn-outline-primary' onClick={this.addFeed}>Add</button>
            </div>
        </div>
    }
}