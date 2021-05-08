import React from 'react';
import { withRouter } from "react-router-dom";

interface LinkProps {
    route: string;
    className?: string;
    as?: string;
    onClick?: Function;
    history: any;
    href?: string;
}

class Link extends React.Component<LinkProps> {

    clickHandler = (e) => {
        e.preventDefault();

        const { onClick, route, history } = this.props;
        if (route) {
            history.push(route);
        }

        if (onClick) {
            onClick(e);
        }
    }

    render() {
        const { className, href, children } = this.props;
        const As = this.props.as || 'a';
        const newProps: any = {
            className: className,
            onClick: this.clickHandler
        };
        if ('a' === As) {
            newProps.href = href || '#';
        }
        if ('button' === As) {
            newProps.type = 'button';
        }

        return <As {...newProps}>{children}</As>
    }

}

export default withRouter(Link);
