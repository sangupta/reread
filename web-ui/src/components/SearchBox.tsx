import React from 'react';
import { withRouter } from 'react-router-dom';
import { RouteComponentProps } from "react-router";

import { collect, WithStoreProp } from 'react-recollect';

interface SearchBoxProps extends WithStoreProp, RouteComponentProps {
}

interface SearchBoxState {
    text: string;
}

class SearchBox extends React.Component<SearchBoxProps, SearchBoxState> {

    state: SearchBoxState = {
        text: ''
    }

    setText = (e: React.FormEvent<HTMLInputElement>) => {
        this.setState({ text: e.currentTarget.value });
    }

    runSearch = (e: React.FormEvent) => {
        e.preventDefault();

        const { text } = this.state;
        if (!text) {
            return;
        }

        this.props.store.searchText = text;
        this.props.history.push('/search');
        return false;
    }

    render() {
        const { text } = this.state;

        return <form className="col-12 col-lg-auto mb-3 mb-lg-0 me-lg-3 col-md-2" onSubmit={this.runSearch}>
            <input type="search" className="form-control form-control-dark" placeholder="Search..." aria-label="Search" value={text} onChange={this.setText} />
        </form>
    }

}

export default withRouter(collect(SearchBox));
