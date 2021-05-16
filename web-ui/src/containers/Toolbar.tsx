import React from 'react';

import Dropdown, { DropDownOption } from '../components/Dropdown';
import Icon from '../components/Icon';

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
    { label: 'Masonry View', value: 'masonry' },
    { label: 'List View', value: 'list' },
    { label: 'Title Only View', value: 'title' },
    { label: 'Magazine View', value: 'magazine' }
];

interface ToolbarProps {
    sortOption: string;
    includeItems: string;
    layout: string;
    showFeedDetails: boolean;
    onMarkAllAs: Function;
    onSortChange: (v: string) => void;
    onIncludeChange: (v: string) => void;
    onLayoutChange: (v: string) => void;
    onRefresh: (e: React.MouseEvent) => void;
    onFeedDetails: (e: React.MouseEvent) => void;
}

interface ToolbarState {

}

export default class Toolbar extends React.Component<ToolbarProps, ToolbarState> {

    markChanged = (value: string) => {
        this.props.onMarkAllAs(value);
    }

    showFeedDetails = () => {
        const { showFeedDetails } = this.props;
        if (!showFeedDetails) {
            return null;
        }

        return <>
            <button type="button" className="btn btn-sm btn-outline-secondary mr-3" onClick={this.props.onFeedDetails}>
                <Icon name='binoculars' /> Feed Details
            </button>
            <div className='px-2' />
        </>
    }

    render() {
        const { sortOption, includeItems, layout } = this.props;

        return <div className='d-flex flex-row mb-2 post-toolbar'>
            {this.showFeedDetails()}

            <button type="button" className="btn btn-sm btn-outline-secondary mr-3" onClick={this.props.onRefresh}>
                <Icon name='arrow-clockwise' /> Refresh
            </button>

            <div className='px-2' />
            <Dropdown variant='secondary' options={markOptions} onSelect={this.markChanged} label='Mark read/unread' />

            <div className='px-2' />
            <Dropdown variant='secondary' options={displayItems} onSelect={this.props.onIncludeChange} value={includeItems} />

            <div className='px-2' />
            <Dropdown variant='secondary' options={sortOptions} onSelect={this.props.onSortChange} value={sortOption} />

            <div className='px-2' />
            <Dropdown variant='secondary' options={layoutOptions} onSelect={this.props.onLayoutChange} value={layout} />
        </div>
    }

}
