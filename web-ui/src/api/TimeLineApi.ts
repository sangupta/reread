import Axios from 'axios';

export default class TimeLineApi {

    static async getTimeLine() {
        return this.fetchTimeline('/posts/all');
    }

    static async getStarsTimeLine() {
        return this.fetchTimeline('/posts/stars');
    }

    static async getBookmarksTimeLine() {
        return this.fetchTimeline('/posts/bookmarks');
    }

    static async getFeedTimeLine(feedID: string) {
        return this.fetchTimeline('/posts/feed/' + feedID);
    }

    static async getFolderTimeLine(folderID: string) {
        return this.fetchTimeline('/posts/folder/' + folderID);
    }

    private static async fetchTimeline(path: string) {
        try {
            const response = await Axios.get(path);
            return response.data;
        } catch (e) {
            return null;
        }
    }
}
