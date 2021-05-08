import React from 'react';

export default class AddFeedView extends React.Component {

    render() {
        return <div className='row'>
            <h1>Add site/feed</h1>

            <form autoComplete='off' className='col-md-6 mt-3'>
                <div className="form-group">
                    <label htmlFor="siteAddress">Site Address</label>
                    <input type="text" className="form-control md-6" aria-describedby="siteAddressHelp" placeholder="http://site-to-follow.com" />
                    <small id="siteAddressHelp" className="form-text text-muted">We will try and find RSS and social media you can follow from the site.</small>
                </div>
                <button type='submit' className='btn btn-primary mt-3'>Discover</button>
            </form>
        </div>
    }
}
