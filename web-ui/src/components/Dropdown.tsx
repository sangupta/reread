import React from 'react';

export interface DropDownOption {
    label: string;
    value: string;
}

interface DropdownItemProps {
    option: DropDownOption;
    onClick: Function;
}

class DropdownItem extends React.Component<DropdownItemProps> {

    clickHandler = () => {
        this.props.onClick(this.props.option.value);
    }

    render() {
        const { option } = this.props;
        return <li>
            <a className="dropdown-item" href="#" onClick={this.clickHandler}>{option.label}</a>
        </li>
    }
}

interface DropdownProps {
    variant: string;
    options: Array<DropDownOption>;
    onSelect: Function;
    value: string;
}

interface DropdownState {
    open: boolean;
}

export default class Dropdown extends React.Component<DropdownProps, DropdownState> {

    state = {
        open: false
    }

    toggleDropdown = () => {
        this.setState(state => {
            return { open: !state.open }
        });
    }

    itemClicked = (value: string) => {
        this.setState({ open: false });
        this.props.onSelect(value);
    }

    render() {
        const { variant, options, value } = this.props;
        const { open } = this.state;

        const selectedOption = options.find(item => item.value === value);
        const dropdownLabel = selectedOption ? selectedOption.label : '';

        return <div className="dropdown">
            <button className={'btn btn-outline-' + variant + ' dropdown-toggle'} type="button" aria-expanded="false" onClick={this.toggleDropdown}>{dropdownLabel}</button>
            <ul className={'dropdown-menu ' + (open ? 'show' : '')}>
                {options.map(option => <DropdownItem option={option} onClick={this.itemClicked} />)}
            </ul>
        </div >
    }
}
