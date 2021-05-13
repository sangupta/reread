import React from 'react';
import Modal from '../components/Modal';
import FeedApi from '../api/FeedApi';
import { XYPlot, XAxis, YAxis, VerticalGridLines, HorizontalGridLines, LineMarkSeries } from 'react-vis';
import DisplayDate from '../components/DisplayDate';

interface FeedDetailsContainerProps {
    details: any;
}

interface FeedDetailsContainerState {
    chart: Array<any>;
}

const normalizeMaxY = function (y) {
    if (y < 5) { return 5; }
    if (y < 10) { return 10; }
    if (y < 50) { return 50; }
    if (y < 100) { return 100; }
    if (y < 500) { return 500; }
    if (y < 1000) { return 1000; }

    const num = "" + y;
    const normalized = num.charAt(0) + "0".repeat(num.length - 3);
    return Number(normalized);
}

export default class FeedDetailsContainer extends React.Component<FeedDetailsContainerProps, FeedDetailsContainerState> {

    state = {
        chart: []
    };

    componentDidMount = async () => {
        const { details } = this.props;

        const data = await FeedApi.getFeedChart(details.feedID);
        this.setState({ chart: data });
    }

    renderChart = () => {
        const { chart } = this.state;
        if (!chart || chart.length === 0) {
            return null;
        }

        let minX = chart[0].time, maxX = chart[0].time;
        let minY = chart[0].value, maxY = chart[0].value;

        for (let index = 1; index < chart.length; index++) {
            const item = chart[index];
            minX = Math.min(minX, item.time);
            maxX = Math.max(maxX, item.time);
            minY = Math.min(minY, item.value);
            maxY = Math.max(maxY, item.value);
        }

        maxY = normalizeMaxY(maxY);

        const data = [];
        for (let index = 0; index < chart.length; index++) {
            const item = chart[index];
            data.push({ x: item.time, y: item.value });
        }

        return <div className='d-flex flex-column feed-details-chart'>
            <XYPlot xDomain={[minX, maxX]} yDomain={[0, maxY]} xType="time" width={800} height={400} >
                <VerticalGridLines />
                <HorizontalGridLines />
                <XAxis />
                <YAxis />
                <LineMarkSeries data={data} />
            </XYPlot>
            <h5 className='chart-label'>Frequency of articles posted</h5>
        </div>
    }

    asLink = (url: string) => {
        if (url) {
            return <a href={url} target='_blank'>{url}</a>;
        }

        return '';
    }

    render() {
        const { details } = this.props;

        return <Modal className='post-view-modal' onCloseModal={this.props.onModalClose}>
            <div className="modal-content post-view-content">
                <div className="modal-header d-flex flex-row feed-details-header">
                    <div className='feed-details'>
                        <h1>{details.title}</h1>
                        <h5>{this.asLink(details.siteUrl)}</h5>
                        <h5>Date subscribed: <DisplayDate millis={details.subscribed} /></h5>
                        <h5>XML address: {this.asLink(details.feedUrl)}</h5>
                        <h5>Last crawled: <DisplayDate millis={details.lastCrawled} showTime={true} /></h5>
                    </div>
                    <button type="button" className="btn-close" aria-label="Close" onClick={this.props.onModalClose} />
                </div>
                <div className='modal-body h-100'>
                    {this.renderChart()}
                </div>
                <div className="modal-footer">
                    <button type="button" className="btn btn-danger">Unsubscribe</button>
                </div>
            </div>
        </Modal>
    }
}
