import React from 'react';

const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

interface DisplayDateProps {
    millis: number;
    showTime?: boolean;
}

const twoDigits = function (x: number): string {
    if (x < 10) {
        return '0' + x;
    }

    return '' + x;
}

export default class DisplayDate extends React.Component<DisplayDateProps, {}> {

    static defaultProps = {
        showTime: false
    }

    render() {
        const { millis, showTime } = this.props;
        if (!millis) {
            return null;
        }

        const d: Date = new Date(millis);
        const year = d.getFullYear();
        const month = d.getMonth();
        const date = d.getDate();

        const datePart = MONTHS[month] + ' ' + twoDigits(date) + ', ' + year;
        if (!showTime) {
            return datePart;
        }

        const hour = d.getHours();
        const min = d.getMinutes();
        const sec = d.getSeconds();

        return datePart + ' ' + twoDigits(hour) + ':' + twoDigits(min) + ':' + twoDigits(sec);
    }
}