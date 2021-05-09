import React from 'react';

interface IconProps {
    name: string;
}

export default class Icon extends React.Component<IconProps, {}> {
    render() {
        return <i className={'bi bi-' + this.props.name}></i>
    }
}
