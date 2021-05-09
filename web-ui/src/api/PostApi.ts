import Axios from 'axios';

export default class PostApi {

    static async search(text:string) {
        const response = await Axios.get('/posts/search?query=' + encodeURIComponent(text));
        return response.data;
    }

}