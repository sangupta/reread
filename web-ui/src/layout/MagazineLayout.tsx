import React from 'react';
import { Post } from '../api/Model';
import TimeAgo from '../components/TimeAgo';

interface MagazineItemProps {
    post: Post;
    onShowPost: (post: Post) => void;
}

interface MagazineItemState {
    postRead: boolean;
}

class MagazineItem extends React.Component<MagazineItemProps, MagazineItemState> {

    constructor(props: MagazineItemProps) {
        super(props);
        this.state = {
            postRead: props.post ? props.post.readOn > 0 : false
        }
    }

    componentDidUpdate(prevProps: any) {
        if (this.props.post === prevProps.post) {
            return;
        }
        const { postRead } = this.state;
        const readOn = this.props.post.readOn > 0;
        if (postRead !== readOn) {
            this.setState({ postRead: readOn });
        }
    }

    showPost = (e: React.MouseEvent) => {
        e.preventDefault();
        this.setState({ postRead: true });
        this.props.onShowPost(this.props.post);
    }

    renderImage(post: Post) {
        if (!post.image || !post.image.url) {
            return null;
        }

        return <img src={post.image.url} className="card-img-top"
            alt={post.title}
        // width={podWidth} height={podHeight} 
        />
    }

    render() {
        const { post } = this.props;
        const { postRead } = this.state;

        return <div className={"card mb-3 card-magazine pointer " + (postRead ? 'card-read' : '')} onClick={this.showPost}>
            <div className="row g-0">
                <div className="col-md-4">
                    {this.renderImage(post)}
                </div>
                <div className="col-md-8">
                    <div className="card-body">
                        <h5 className="card-title">{post.title}</h5>
                        <p className="card-text">{post.snippet}</p>
                        <p className="card-text"><small className="text-muted"><TimeAgo millis={post.updated} /></small></p>
                    </div>
                </div>
            </div>
        </div>;
    }
}

interface MagazineLayoutProps {
    posts: Array<Post>;
    onShowPost: (post: Post) => void;
}

export default class MagazineLayout extends React.Component<MagazineLayoutProps> {

    render() {
        const { posts } = this.props;

        return <div className='list-group list-group-flush scrollarea'>
            {posts.map(item =>
                <MagazineItem key={item.feedPostID}
                    post={item}
                    onShowPost={this.props.onShowPost} />
            )}
        </div>
    }
}
