import React from 'react';
import Modal from '../components/Modal';

interface FeedDetailsContainerProps {
    details: any;
}

export default class FeedDetailsContainer extends React.Component<FeedDetailsContainerProps, {}> {

    render() {
        const { details } = this.props;

        return <Modal onCloseModal={this.props.onModalClose}>
            <div className="modal-content post-view-content">
                <div className="modal-header d-flex flex-row">
                    <div className='feed-details'>
                        {details.title}
                        <br />
                        {details.siteUrl}
                        <br />
                        {details.subscribed}
                    </div>
                    <button type="button" className="btn-close" aria-label="Close" onClick={this.props.onModalClose} />
                </div>
                <div className='modal-body h-100'>
                    Feed address: {details.feedUrl}
                    <br />
                    Last crawled: {details.lastCrawled}
                </div>
            </div>
        </Modal>
    }
}
