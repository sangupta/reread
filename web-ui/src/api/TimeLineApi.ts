import Axios from 'axios';

export default class TimeLineApi {

    static async getTimeLine() {
        const response = await Axios.get('/posts/all');
        return response.data;
    }

    static async getFeedTimeLine(feedID: string) {
        const response = await Axios.get('/posts/feed/' + feedID);
        return response.data;
    }

    static async getFolderTimeLine(folderID: string) {
        const response = await Axios.get('/posts/folder/' + folderID);
        return response.data;
    }

}