import React from 'react';
import { collect, store } from 'react-recollect';

import Link from './../components/Link';

class Header extends React.Component {

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

                    <form className="col-12 col-lg-auto mb-3 mb-lg-0 me-lg-3">
                        <input type="search" className="form-control form-control-dark" placeholder="Search..." aria-label="Search" />
                    </form>

                    <div className="text-end">
                        <Link className="btn btn-primary me-2" route='/addFeed'>+ Add</Link>
                        <Link className="btn btn-secondary me-2" route='/settings'>
                            <i className="fas fa-cogs"></i>
                        </Link>
                    </div>
                </div>
            </div>
        </header>
    }

}

export default collect(Header);
