import React from 'react';

interface IconProps {
    name: string;
    className?: string;
}

export default class Icon extends React.Component<IconProps, {}> {
    render() {
        const { name, className: css } = this.props;
        return <i className={'bi bi-' + name + (css ? ' ' + css : '')}></i>
    }
}
