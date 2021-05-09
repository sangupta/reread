import React from 'react';

interface AlertProps {
    level?: string;
}

export default class Alert extends React.Component<AlertProps, {}> {

    render() {
        const level = this.props.level || 'info';
        return <div className={'alert alert-' + level}>{this.props.children}</div>
    }

}
