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
        return <li key={option.value}>
            <a className="dropdown-item" href="#" onClick={this.clickHandler}>{option.label}</a>
        </li>
    }
}

interface DropdownProps {
    variant: string;
    options: Array<DropDownOption>;
    onSelect: Function;
    value?: string;
    label?: string;
}

interface DropdownState {
    open: boolean;
}

export default class Dropdown extends React.Component<DropdownProps, DropdownState> {

    wrapperRef: React.RefObject<any>;

    constructor(props: DropdownProps) {
        super(props);

        this.wrapperRef = React.createRef();
        this.state = {
            open: false
        }
    }

    componentDidMount() {
        document.addEventListener('mousedown', this.handleClickOutside);
    }

    componentWillUnmount() {
        document.removeEventListener('mousedown', this.handleClickOutside);
    }

    handleClickOutside = (event: any) => {
        if (this.wrapperRef && !this.wrapperRef.current.contains(event.target)) {
            this.setState({ open: false });
        }
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
        const { variant, options, value, label } = this.props;
        const { open } = this.state;

        const selectedOption = options.find(item => item.value === value);
        const dropdownLabel = selectedOption ? selectedOption.label : label;

        return <div className="dropdown" ref={this.wrapperRef}>
            <button className={'btn btn-outline-' + variant + ' btn-sm dropdown-toggle'} type="button" aria-expanded="false" onClick={this.toggleDropdown}>{dropdownLabel}</button>
            <ul className={'dropdown-menu ' + (open ? 'show' : '')}>
                {options.map(option => <DropdownItem key={option.value} option={option} onClick={this.itemClicked} />)}
            </ul>
        </div >
    }
}
