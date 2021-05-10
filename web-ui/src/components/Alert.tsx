import React from 'react';

interface AlertProps {
    level?: string;
    className?: string;
}

export default class Alert extends React.Component<AlertProps, {}> {

    render() {
        const level = this.props.level || 'info';
        return <div className={'alert alert-' + level + ' ' + (this.props.className || '')}>{this.props.children}</div>
    }

}
