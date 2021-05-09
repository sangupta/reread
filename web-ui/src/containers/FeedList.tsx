import React from 'react';
import { collect, WithStoreProp } from 'react-recollect';

import FolderItems from './../components/FolderItems';
import FeedItem from '../components/FeedItem';

interface FeedListProps extends WithStoreProp {

}

class FeedList extends React.Component<FeedListProps> {

    showFolders = () => {
        const { store } = this.props;
        if (!store.folders || store.folders.length === 0) {
            return null;
        }

        return <>
            {store.folders.map(folder => <FolderItems folder={folder} />)}
            <li className="border-top my-3"></li>
        </>
    }

    showFeeds = () => {
        const { store } = this.props;
        if (!store.feeds || store.feeds.length === 0) {
            return null;
        }

        return <>
            {store.feeds.map(feed => <FeedItem feed={feed} />)}
        </>
    }

    render() {
        return <>
            <div className="p-3 bg-white" style={{ width: '280px' }}>
                <ul className="list-unstyled ps-0">
                    {this.showFolders()}

                    {this.showFeeds()}
                </ul>
            </div>
        </>
    }

}

export default collect(FeedList);
