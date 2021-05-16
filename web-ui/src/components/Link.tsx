import React from 'react';
import { withRouter } from "react-router-dom";
import { RouteComponentProps } from "react-router";

interface LinkProps extends RouteComponentProps {
    route: string;
    className?: string;
    as?: string;
    onClick?: Function;
    href?: string;
}

class Link extends React.Component<LinkProps> {

    clickHandler = (e: React.MouseEvent) => {
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
