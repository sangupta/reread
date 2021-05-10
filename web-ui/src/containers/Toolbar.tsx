import React from 'react';
import { collect, WithStoreProp } from 'react-recollect';

import Dropdown, { DropDownOption } from '../components/Dropdown';

const markOptions: Array<DropDownOption> = [
    { label: 'Mark all as read', value: 'markRead' },
    { label: 'Mark all as unread', value: 'markUnread' }
];

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
    onMarkAllAs: Function;
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

    markChanged = (value: string) => {
        this.props.onMarkAllAs(value);
    }

    render() {
        const { sortOption, displayItem, layout } = this.props.store;

        return <div className='d-flex flex-row mb-2'>
            <Dropdown variant='secondary' options={markOptions} onSelect={this.markChanged} label='Mark read/unread' />

            <div className='px-2' />
            <Dropdown variant='secondary' options={displayItems} onSelect={this.displaySelected} value={displayItem} />

            <div className='px-2' />
            <Dropdown variant='secondary' options={sortOptions} onSelect={this.sortSelected} value={sortOption} />

            <div className='px-2' />
            <Dropdown variant='secondary' options={layoutOptions} onSelect={this.layoutSelected} value={layout} />
        </div>
    }

}

export default collect(Toolbar);