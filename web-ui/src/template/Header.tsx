import React from 'react';
import { collect, store } from 'react-recollect';

import Link from './../components/Link';
import SearchBox from './../components/SearchBox';
import Icon from '../components/Icon';

/**
 * Render the header bar.
 */
class Header extends React.Component {

    /**
     * Handler called when the toggle feed list button
     * is clicked in top-left of the header.
     */
    toggleFeedList = () => {
        store.showFeedList = !store.showFeedList;
    }

    render() {
        return <header className="p-1 mb-auto border-bottom">
            <div className="container-fluid">
                <div className="d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">
                    <button type="button" className="btn btn-outline" onClick={this.toggleFeedList}>
                        <Icon name='layout-sidebar' />
                    </button>

                    <ul className="nav col-12 col-lg-auto me-lg-auto mb-2 justify-content-center mb-md-0">
                        <li>
                            <Link className="nav-link px-2 text-primary" route='/'>
                                <Icon name='rss-fill' /> ReRead
                            </Link>
                        </li>
                    </ul>

                    <SearchBox />

                    <div className="text-end">
                        <Link className="btn btn-primary me-2" route='/addFeed'>+ Add Site</Link>
                        <Link className="btn btn-info me-2" route='/activity'>
                            <Icon name='graph-up' label='View Activity' />
                        </Link>
                    </div>
                </div>
            </div>
        </header>
    }

}

export default collect(Header);
