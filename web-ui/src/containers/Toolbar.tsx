import React from 'react';
import { collect, WithStoreProp } from 'react-recollect';

import Dropdown, { DropDownOption } from '../components/Dropdown';
import Icon from '../components/Icon';

const sortOptions: Array<DropDownOption> = [
    { label: 'Newest first', value: 'newest' },
    { label: 'Oldest first', value: 'oldest' }
];

const displayItems: Array<DropDownOption> = [
    { label: 'All items', value: 'all' },
    { label: 'Unread only', value: 'unread' },
    { label: 'Read only', value: 'read' },
];

const layoutOptions: Array<DropDownOption> = [
    { label: 'Cards View', value: 'cards' },
    { label: 'List View', value: 'list' }
];

interface ToolbarProps extends WithStoreProp {

}

class Toolbar extends React.Component<ToolbarProps, {}> {

    sortSelected = (value: string) => {
        this.props.store.sortOption = value as string;
    }

    displaySelected = (value: string) => {
        this.props.store.displayItem = value as string;
    }

    layoutSelected = (value: string) => {
        this.props.store.layout = value as string;
    }

    render() {
        const { sortOption, displayItem, layout } = this.props.store;

        return <div className='d-flex flex-row mb-2'>
            <button type='button' className='btn btn-outline-secondary'>Mark all as read</button>
            <div className='px-2' />
            <Dropdown variant='secondary' options={displayItems} onSelect={this.displaySelected} value={displayItem} />
            <div className='px-2' />
            <div className='btn-group'>
                <button className='btn btn-outline-secondary'>
                    <Icon name='arrow-left-circle' /> Prev
                </button>
                <button className='btn btn-outline-secondary'>
                    <Icon name='arrow-right-circle' /> Next
                </button>
            </div>
            <div className='px-2' />
            <Dropdown variant='secondary' options={sortOptions} onSelect={this.sortSelected} value={sortOption} />
            <div className='px-2' />
            <Dropdown variant='secondary' options={layoutOptions} onSelect={this.layoutSelected} value={layout} />
        </div>
    }

}

export default collect(Toolbar);