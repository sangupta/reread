import * as React from 'react';

const ONE_SECOND: number = 1000;
const ONE_MINUTE: number = 60 * ONE_SECOND;
const ONE_HOUR: number = 60 * ONE_MINUTE;
const ONE_DAY: number = 24 * ONE_HOUR;
const ONE_WEEK: number = 7 * ONE_DAY;
const ONE_MONTH: number = 30 * ONE_DAY;
const ONE_YEAR: number = 365 * ONE_DAY;

interface TimeAgoProps {
    millis: number;
}

export default class TimeAgo extends React.Component<TimeAgoProps, {}> {

    format(delta: number, unit: number, suffix: string): string {
        const value = Math.floor(delta / unit);

        if (value <= 1) {
            if(suffix === 'hour') {
                return "an " + suffix + " ago";
            }
            return "a " + suffix + " ago";
        }

        return value + " " + suffix + "s ago";
    }

    render() {
        if (!this.props.millis) {
            return null;
        }

        const current: number = Date.now();
        const delta: number = current - this.props.millis;
        if (delta < 0) {
            return null;
        }

        if (delta < ONE_MINUTE) {
            return "moments ago";
        }

        if (delta < ONE_HOUR) {
            return this.format(delta, ONE_MINUTE, "minute");
        }

        if (delta < ONE_DAY) {
            return this.format(delta, ONE_HOUR, "hour");
        }

        if (delta < ONE_WEEK) {
            return this.format(delta, ONE_DAY, "day");
        }

        if (delta < ONE_MONTH) {
            return this.format(delta, ONE_WEEK, "week");
        }

        if (delta < ONE_YEAR) {
            return this.format(delta, ONE_MONTH, "month");
        }

        return this.format(delta, ONE_YEAR, "year");
    }

}