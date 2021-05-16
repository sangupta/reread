import React from 'react';
import OpmlApi from '../api/OpmlApi';
import { OpmlFeed } from '../api/Model';

interface ImportOpmlContainerState {
    opmlFile: any;
    opmlFeeds: Array<OpmlFeed>;
}

export default class ImportOpmlContainer extends React.Component<{}, ImportOpmlContainerState> {

    /**
     * Default state
     */
    state: ImportOpmlContainerState = {
        opmlFile: null,
        opmlFeeds: []
    };

    setOpmlFile = (e: any) => {
        this.setState({ opmlFile: e.target.files[0] });
    }

    confirmImport = async () => {
        this.importOpmlFile(true);
    }

    importOpml = () => {
        this.importOpmlFile(false);
    }

    importOpmlFile = async (confirm: boolean) => {
        const file = this.state.opmlFile;
        if (!file) {
            return;
        }

        const data: Array<OpmlFeed> = await OpmlApi.importOpml(file, confirm);
        this.setState({ opmlFeeds: data });
    }

    showDiscoveredFeeds = () => {
        const { opmlFeeds } = this.state;
        if (!opmlFeeds || opmlFeeds.length === 0) {
            return null;
        }

        return <div className='h-100'>
            <div className='opml-feed-list mt-3'>
                <h2>Discovered Feeds</h2>

                <div className='opml-feeds-container scrollable'>
                    <ul>
                        {opmlFeeds.map((item, index) => <li key={index}>
                            <a key={item.xmlUrl} href={item.xmlUrl}>{item.title}</a> (<a href={item.htmlUrl}>site</a>)
                    {this.showChildFeeds(item)}
                        </li>)}
                    </ul>
                </div>
            </div>
        </div>
    }

    showChildFeeds = (feed: OpmlFeed) => {
        if (!feed.children || feed.children.length === 0) {
            return null;
        }

        return <ul>
            {feed.children.map(item => <li key={item.xmlUrl} >
                <a href={item.xmlUrl}>{item.title}</a> (<a href={item.htmlUrl}>site</a>)
            </li>)}
        </ul>
    }

    render() {
        const { opmlFeeds } = this.state;
        const hasFeeds = opmlFeeds && opmlFeeds.length > 0;

        return <>
            <h1>Import OPML file</h1>
            <form autoComplete='off' className='mt-3'>
                <div className="form-group">
                    <label htmlFor="opmlFile">OPML file</label>
                    <input id='opmlFile' type="file" className="form-control md-6" aria-describedby="opmlFileHelp" onChange={this.setOpmlFile} />
                    <small id="opmlFileHelp" className="form-text text-muted">Any existing feed present in OPML file will be ignored.</small>
                </div>

                <button type='button' className='btn btn-primary mt-3' onClick={this.importOpml} disabled={hasFeeds}>Discover</button>

                {hasFeeds && <button type='button' className='btn btn-primary mt-3 mx-3' onClick={this.confirmImport}>Import All</button>}

            </form>

            {this.showDiscoveredFeeds()}
        </>
    }

}
