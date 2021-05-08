import React from 'react';
import { BrowserRouter, Switch, Route } from "react-router-dom";

import Header from './template/Header';
import Footer from './template/Footer';
import AddFeedView from './views/AddFeedView';
import HomeView from './views/HomeView';

export default class App extends React.Component {

    render() {
        return <>
            <Header />
            <BrowserRouter>
                <main role='main' className='inner cover w-100 h-100 px-3'>
                    <Switch>
                        <Route exact path='/addFeed' component={AddFeedView} />
                        <Route component={HomeView} />
                    </Switch>
                </main>
            </BrowserRouter>
            <Footer />
        </>
    }

}
