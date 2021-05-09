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
                        <i className="fas fa-bars" />
                    </button>

                    <ul className="nav col-12 col-lg-auto me-lg-auto mb-2 justify-content-center mb-md-0">
                        <li>
                            <Link className="nav-link px-2 text-primary" route='/'>
                                <i className="fas fa-rss-square" /> ReRead
                            </Link>
                        </li>
                    </ul>

                    <button type="button" className="btn btn-outline px-3">
                        <i className="fas fa-sync-alt"></i> Refresh
                    </button>

                    <SearchBox />

                    <div className="text-end">
                        <Link className="btn btn-primary me-2" route='/addFeed'>+ Add</Link>
                        <Link className="btn btn-secondary me-2" route='/settings'>
                            <Icon name='gear' />
                        </Link>
                    </div>
                </div>
            </div>
        </header>
    }

}

export default collect(Header);
