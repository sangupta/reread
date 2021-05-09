import React from 'react';
import { BrowserRouter, Switch, Route } from "react-router-dom";
import { collect, WithStoreProp } from 'react-recollect';

import Header from './template/Header';

import AddFeedView from './views/AddFeedView';
import HomeView from './views/HomeView';
import SettingsView from './views/SettingsView';
import FeedLoader from './containers/FeedLoader';

interface AppProps extends WithStoreProp {

}

interface AppState {
    loading: boolean;
}

class App extends React.Component<AppProps, AppState> {

    state = {
        loading: true
    }

    componentDidMount = async () => {
        this.setState({ loading: false });
    }

    render() {
        const text = this.props.store.searchText;
        const { loading } = this.state;
        if (loading) {
            return "Loading...";
        }

        return <>
            <BrowserRouter>
                <Header />
                <main role='main' className='inner cover w-100 h-100 px-3'>
                    <Switch>
                        <Route exact path='/addFeed' component={AddFeedView} />
                        <Route exact path='/settings' component={SettingsView} />

                        <Route exact path='/feeds/all'>
                            <HomeView><FeedLoader key='feedLoader' mode='all' /></HomeView>
                        </Route>

                        <Route exact path='/search'>
                            <HomeView><FeedLoader key='feedLoader' mode='search' query={text} /></HomeView>
                        </Route>

                        <Route exact path='/feed/:feedID'>
                            <HomeView><FeedLoader key='feedLoader' mode='feed' /></HomeView>
                        </Route>

                        <Route exact path='/folder/:folderID'>
                            <HomeView><FeedLoader key='feedLoader' mode='folder' /></HomeView>
                        </Route>

                        <Route>
                            <HomeView><FeedLoader key='feedLoader' mode='all' /></HomeView>
                        </Route>
                    </Switch>
                </main>
            </BrowserRouter>
        </>
    }

}

export default collect(App);