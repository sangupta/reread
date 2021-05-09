import React from 'react';
import { collect, WithStoreProp } from 'react-recollect';
import ListLayout from '../layout/ListLayout';
import { Post } from '../api/Model';

interface ContentPaneProps extends WithStoreProp {
    posts: Array<Post>;
}

class ContentPane extends React.Component<ContentPaneProps, any> {

    render() {
        const { posts, store } = this.props;
        const layout = store.layout;

        let Element;
        switch(layout) {
            case 'cards':
            case 'list':
                Element = ListLayout;
        }

        return <div className='content-pane'>
            <Element posts={posts} />
        </div>
    }

}

export default collect(ContentPane);
