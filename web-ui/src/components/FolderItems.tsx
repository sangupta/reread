import React from 'react';
import { Folder } from '../api/Model';

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

    toggleFolder = () => {
        this.setState({ open: !this.state.open });
    }

    render() {
        const { folder } = this.props;
        const { open } = this.state;

        return <li className="mb-1">
            <button className="btn btn-toggle align-items-center rounded" onClick={this.toggleFolder}>{folder.title}</button>
            <div className={'collapse ' + (open ? 'show' : '')}>
                <ul className="btn-toggle-nav list-unstyled fw-normal pb-1 small">
                    <li><a href="#" className="link-dark rounded">Overview</a></li>
                    <li><a href="#" className="link-dark rounded">Updates</a></li>
                    <li><a href="#" className="link-dark rounded">Reports</a></li>
                </ul>
            </div>
        </li>;
    }

}
