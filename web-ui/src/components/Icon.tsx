import React from 'react';

interface IconProps {
    name: string;
    className?: string;
    label?: string;
}

export default class Icon extends React.Component<IconProps, {}> {
    render() {
        const { name, label, className: css } = this.props;
        return <>
            <i className={'bi bi-' + name + (css ? ' ' + css : '')}></i>&nbsp;{label}
        </>
    }
}
