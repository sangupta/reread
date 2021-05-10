import React from 'react';

import AddFeedContainer from '../containers/AddFeedContainer';
import ImportOpmlContainer from '../containers/ImportOpmlContainer';

/**
 * View used when add feed button is clicked in the top bar
 */
export default class AddFeedView extends React.Component<{}, {}> {

    state = {
        container: 'feed'
    }

    showAddFeed = () => {
        this.setState({ container: 'feed' });
    }

    showOpmlImport = () => {
        this.setState({ container: 'opml' });
    }

    showContainer = () => {
        const { container } = this.state;
        if ('feed' === container) {
            return <AddFeedContainer />
        }

        if ('opml' === container) {
            return <ImportOpmlContainer />
        }

        return null;
    }

    render() {
        const { container } = this.state;

        return <div className='row mt-3'>
            <div className='col mx-4'>
                <ul className="nav nav-tabs">
                    <li className="nav-item">
                        <a className={'nav-link ' + ('feed' === container ? 'active' : '')} href="#" onClick={this.showAddFeed}>Add Site</a>
                    </li>
                    <li className="nav-item">
                        <a className={'nav-link ' + ('opml' === container ? 'active' : '')} href="#" onClick={this.showOpmlImport}>Import OPML file</a>
                    </li>
                </ul>

                <div className='row'>
                    <div className='col add-container'>
                        {this.showContainer()}
                    </div>
                </div>
            </div>
        </div>
    }
}
