import Axios from 'axios';

export default class TimeLineApi {

    static async getTimeLine() {
        return null;
    }

    static async getFeedTimeLine(feedID: string) {
        const response = await Axios.get('/posts/feed/' + feedID);
        return response.data;
    }

    static async getFolderTimeLine(folderID: string) {
        return null;
    }

}
