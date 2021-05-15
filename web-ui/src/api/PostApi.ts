import Axios from 'axios';
import { Post } from './Model';
import TimeLineApi from './TimeLineApi';

export default class PostApi {

    static async search(text: string): Promise<Array<Post>> {
        const response = await Axios.get('/posts/search?query=' + encodeURIComponent(text));
        return response.data;
    }

    static async toggleMarking(posts:Array<Post>, mode:string):Promise<Array<Post>> {
        const ids: Array<string> = [];
        posts.forEach(post => {
            ids.push((post as Post).feedPostID);
        });

        if ('markRead' === mode) {
            return await PostApi.markAllRead(ids);
        }

        return await PostApi.markAllUnread(ids);
    }

    static async markRead(postID: string): Promise<Post> {
        const response = await Axios.get('/posts/read/' + postID);
        return response.data;
    }

    static async markAllRead(ids: Array<string>): Promise<Array<Post>> {
        const response = await Axios.post('/posts/markAllRead', ids);
        return response.data;
    }

    static async markAllUnread(ids: Array<string>): Promise<Array<Post>> {
        const response = await Axios.post('/posts/markAllUnread', ids);
        return response.data;
    }

    static async toggleStarPost(id: string, isCurrentlyStarred: boolean): Promise<Post> {
        const path = isCurrentlyStarred ? '/posts/unstar/' : '/posts/star/';
        const response = await Axios.get(path + id);
        return response.data;
    }

    static async toggleBookmarkPost(id: string, isCurrentlyBookmarked: boolean): Promise<Post> {
        const path = isCurrentlyBookmarked ? '/posts/unbookmark/' : '/posts/bookmark/';
        const response = await Axios.get(path + id);
        return response.data;
    }

    static async getPosts(mode:string, queryText:string, feedID:string, folderID:string, sortOption: string, includeItems: string, afterPostID: string):Promise<Array<Post>> {
        if (mode === 'search') {
            return await PostApi.search(queryText);
        }

        if (mode === 'all') {
            return await TimeLineApi.getTimeLine(sortOption, includeItems, afterPostID);
        }

        if (mode === 'stars') {
            return await TimeLineApi.getStarsTimeLine(sortOption, includeItems, afterPostID);
        }

        if (mode === 'bookmarks') {
            return await TimeLineApi.getBookmarksTimeLine(sortOption, includeItems, afterPostID);
        }

        if (mode === 'feed') {
            return await TimeLineApi.getFeedTimeLine(feedID, sortOption, includeItems, afterPostID);
        }
        if (mode === 'folder') {
            return await TimeLineApi.getFolderTimeLine(folderID, sortOption, includeItems, afterPostID);
        }

        return [];
    }
}
