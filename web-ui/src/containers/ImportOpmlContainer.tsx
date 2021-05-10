import React from 'react';
import OpmlApi from '../api/OpmlApi';

interface ImportOpmlContainerState {
    opmlFile: any;
}

export default class ImportOpmlContainer extends React.Component<{}, ImportOpmlContainerState> {

    /**
     * Default state
     */
    state = {
        opmlFile: null,
        opmlFeeds: null
    };

    setOpmlFile = (e) => {
        this.setState({ opmlFile: e.target.files[0] });
    }

    importOpml = async () => {
        const file = this.state.opmlFile;
        if (!file) {
            return;
        }

        const data = await OpmlApi.importOpml(file);
        this.setState({ opmlFeeds: data });
    }

    render() {
        return <>
            <h1>Import OPML file</h1>
            <form autoComplete='off' className='mt-3'>
                <div className="form-group">
                    <label htmlFor="opmlFile">OPML file</label>
                    <input id='opmlFile' type="file" className="form-control md-6" aria-describedby="opmlFileHelp" onChange={this.setOpmlFile} />
                    <small id="opmlFileHelp" className="form-text text-muted">Any existing feed present in OPML file will be ignored.</small>
                </div>
                <button type='button' className='btn btn-primary mt-3' onClick={this.importOpml}>Import</button>
            </form>
        </>
    }

}
