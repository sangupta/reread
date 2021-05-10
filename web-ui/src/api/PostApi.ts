import Axios from 'axios';
import { Post } from './Model';

export default class PostApi {

    static async search(text: string): Promise<Post> {
        const response = await Axios.get('/posts/search?query=' + encodeURIComponent(text));
        return response.data;
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

    static async starPost(id: string): Promise<Post> {
        const response = await Axios.get('/posts/star/' + id);
        return response.data;
    }

}
