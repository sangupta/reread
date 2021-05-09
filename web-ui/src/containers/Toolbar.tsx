import React from 'react';
import Dropdown, { DropDownOption } from '../components/Dropdown';

const sortOptions: Array<DropDownOption> = [
    { label: 'Newest first', value: 'newest' },
    { label: 'Oldest first', value: 'oldest' }
];

const displayItems: Array<DropDownOption> = [
    { label: 'All items', value: 'all' },
    { label: 'Unread only', value: 'unread' }
];

const layoutOptions: Array<DropDownOption> = [
    { label: 'Cards View', value: 'cards' },
    { label: 'List View', value: 'list' }
]

interface ToolbarState {
    sortOption: string;
    display: string;
    layout: string;
}

export default class Toolbar extends React.Component<{}, ToolbarState> {

    state = {
        sortOption: 'newest',
        display: 'all',
        layout:'list'
    }

    sortSelected = (value: string) => {
        this.setState({ sortOption: value });
    }

    displaySelected = (value: string) => {
        this.setState({ display: value });
    }

    layoutSelected = (value: string) => {
        this.setState({ layout: value });
    }

    render() {
        return <div className='d-flex flex-row mb-2'>
            <button type='button' className='btn btn-outline-secondary'>Mark all as read</button>
            <div className='px-2' />
            <Dropdown variant='secondary' options={displayItems} onSelect={this.displaySelected} value={this.state.display} />
            <div className='px-2' />
            <div className='btn-group'>
                <button className='btn btn-outline-secondary'>Prev</button>
                <button className='btn btn-outline-secondary'>Next</button>
            </div>
            <div className='px-2' />
            <Dropdown variant='secondary' options={sortOptions} onSelect={this.sortSelected} value={this.state.sortOption} />
            <div className='px-2' />
            <Dropdown variant='secondary' options={layoutOptions} onSelect={this.layoutSelected} value={this.state.layout} />
        </div>
    }

}
