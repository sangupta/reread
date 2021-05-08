import React from 'react';
import ReactDOM from 'react-dom';
import Header from './template/Header';
import Footer from './template/Footer';
import ContentPane from './template/ContentPane';

const App = () => {
    return <>
        <Header />
        <ContentPane />
        <Footer />
    </>
}

ReactDOM.render(<App />, document.body);
