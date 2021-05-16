import React from 'react';
import { XYPlot, XAxis, YAxis, VerticalGridLines, HorizontalGridLines, LineMarkSeries } from 'react-vis';

import Dropdown, { DropDownOption } from '../components/Dropdown';

import FeedApi from '../api/FeedApi';
import Loading from '../components/Loading';
import Alert from '../components/Alert';
import { ChartData } from '../api/Model';

const activityDropdown: Array<DropDownOption> = [
    { label: 'Posts read', value: 'read' },
    { label: 'Post starred', value: 'star' },
    { label: 'Post bookmarked', value: 'bookmark' }
];

const typeOptions: Array<DropDownOption> = [
    { label: 'Count', value: 'count' },
    { label: 'Min', value: 'min' },
    { label: 'Max', value: 'max' },
    { label: 'Average', value: 'avg' }
];

const intervalOptions: Array<DropDownOption> = [
    { label: 'Minute', value: '1' },
    { label: 'Hour', value: '60' },
    { label: 'Day', value: '720' },
    { label: 'Week', value: '5040' }
];

interface ActivityViewState {
    chart: Array<ChartData>;
    activity: string;
    loading: boolean;
    type: string;
    interval: string;
}

const normalizeMaxY = function (y: number): number {
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

export default class ActivityView extends React.Component<{}, ActivityViewState> {

    state: ActivityViewState = {
        chart: [],
        activity: 'read',
        loading: true,
        type: 'count',
        interval: '1'
    }

    componentDidMount() {
        this.fetchData();
    }

    fetchData = async () => {
        const { activity, type, interval } = this.state;

        const data: Array<any> = await FeedApi.getActivityChart(activity, type, interval);
        this.setState({ chart: data, loading: false });
    }

    renderChart = () => {
        const { chart } = this.state;

        if (!chart || chart.length === 0) {
            return <Alert level='info'>No chart data available currently</Alert>
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
            <h5 className='chart-label'>Frequency of articles read</h5>
        </div>
    }

    changeActivity = (e: string) => {
        this.setState({ activity: e }, () => {
            this.fetchData();
        });
    }

    changeInterval = (e: string) => {
        this.setState({ interval: e }, () => {
            this.fetchData();
        });
    }

    changeType = (e: string) => {
        this.setState({ type: e }, () => {
            this.fetchData();
        });
    }

    renderToolbar = () => {
        const { activity, type, interval } = this.state;

        return <div className='d-flex flex-row activity-toolbar align-baseline'>
            <div className='form-label px-2'>Choose Activity</div>
            <Dropdown variant='secondary' options={activityDropdown} onSelect={this.changeActivity} value={activity} />

            <div className='form-label px-2'>Chart Type</div>
            <Dropdown variant='secondary' options={typeOptions} onSelect={this.changeType} value={type} />

            <div className='form-label px-2'>Frequency</div>
            <Dropdown variant='secondary' options={intervalOptions} onSelect={this.changeInterval} value={interval} />
        </div>
    }

    render() {
        const { loading } = this.state;
        if (loading) {
            return <Loading />
        }

        return <div className='activity-container'>
            {this.renderToolbar()}
            {this.renderChart()}
        </div>
    }

}
