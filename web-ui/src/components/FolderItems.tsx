import React from 'react';
import { Folder, Feed } from '../api/Model';
import Icon from './Icon';
import FeedItem from './FeedItem';

interface FolderItemsProps {
    folder: Folder;
}

interface FolderItemsState {
    open: boolean;
}

export default class FolderItems extends React.Component<FolderItemsProps, FolderItemsState> {

    state = {
        open: false
    }

    toggleFolder = (e: React.MouseEvent) => {
        e.preventDefault();
        this.setState({ open: !this.state.open });
    }

    showChildren = () => {
        const { folder } = this.props;
        const hasChildren = folder.childFeeds && folder.childFeeds.length > 0;
        if (!hasChildren) {
            return null;
        }


        const items = [];

        if(folder.childFeeds.length > 1) {
            const folderAsFeed: Feed = {
                masterFeedID: folder.folderID,
                title: 'All'
            };
            items.push(<FeedItem key={'all-' + folder.folderID} feed={folderAsFeed} mode='folder' />);
        }

        folder.childFeeds.forEach(item => {
            items.push(<FeedItem key={item.masterFeedID} feed={item} />);
        });
        return items;
    }

    render() {
        const { folder } = this.props;
        const { open } = this.state;

        return <li className="mb-1">

            <a href="#" className="folder-toggle link-dark rounded" onClick={this.toggleFolder}>
                <Icon name={open ? 'chevron-down' : 'chevron-right'} label={folder.title} />
            </a>
            <div className={'collapse ' + (open ? 'show' : '')}>
                <ul className="btn-toggle-nav list-unstyled fw-normal pb-1 small">
                    {this.showChildren()}
                </ul>
            </div>
        </li>;
    }

}
