import React from 'react';

export default class Header extends React.Component {

    render() {
        return <header className="p-1 mb-auto border-bottom">
            <div className="container-fluid">
                <div className="d-flex flex-wrap align-items-center justify-content-center justify-content-lg-start">
                    <a href="/" className="d-flex align-items-center mb-2 mb-lg-0 text-decoration-none">
                        <i className="fas fa-rss-square"></i>
                    </a>

                    <ul className="nav col-12 col-lg-auto me-lg-auto mb-2 justify-content-center mb-md-0">
                        <li><a href="#" className="nav-link px-2 text-primary">ReRead</a></li>
                    </ul>

                    <button type="button" className="btn btn-outline">
                        <i className="fas fa-sync-alt"></i> Refresh
                    </button>
                    <form className="col-12 col-lg-auto mb-3 mb-lg-0 me-lg-3">
                        <input type="search" className="form-control form-control-dark" placeholder="Search..." aria-label="Search" />
                    </form>

                    <div className="text-end">
                        <button type="button" className="btn btn-primary me-2">+ Add</button>
                    </div>
                </div>
            </div>
        </header>
    }

}
