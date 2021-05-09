import React from 'react';
import { BrowserRouter, Switch, Route } from "react-router-dom";
import { collect, WithStoreProp } from 'react-recollect';

import Header from './template/Header';
import Footer from './template/Footer';

import AddFeedView from './views/AddFeedView';
import HomeView from './views/HomeView';
import SettingsView from './views/SettingsView';
import FeedLoader from './views/FeedLoader';
import FeedFolderView from './views/FeedFolderView';

import FeedApi from './api/FeedApi';

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
        const data = await FeedApi.getFeedList();

        const { store } = this.props;
        store.folders = data.folders;
        store.feeds = data.feeds;

        this.setState({ loading: false });
    }

    render() {
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
                        <Route exact path='/feed/:feedID'>
                            <HomeView><FeedLoader mode='feed' /></HomeView>
                        </Route>
                        <Route exact path='/folder/:folderID'>
                            <HomeView><FeedLoader mode='folder' /></HomeView>
                        </Route>

                        <Route component={HomeView} />
                    </Switch>
                </main>
                <Footer />
            </BrowserRouter>
        </>
    }

}

export default collect(App);
