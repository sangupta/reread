import Axios from 'axios';

export default class TimeLineApi {

    static async getTimeLine(sortOption: string, includeItems: string, afterPostID: string) {
        return this.fetchTimeline('/posts/all', sortOption, includeItems, afterPostID);
    }

    static async getStarsTimeLine(sortOption: string, includeItems: string, afterPostID: string) {
        return this.fetchTimeline('/posts/stars', sortOption, includeItems, afterPostID);
    }

    static async getBookmarksTimeLine(sortOption: string, includeItems: string, afterPostID: string) {
        return this.fetchTimeline('/posts/bookmarks', sortOption, includeItems, afterPostID);
    }

    static async getFeedTimeLine(feedID: string, sortOption: string, includeItems: string, afterPostID: string) {
        return this.fetchTimeline('/posts/feed/' + feedID, sortOption, includeItems, afterPostID);
    }

    static async getFolderTimeLine(folderID: string, sortOption: string, includeItems: string, afterPostID: string) {
        return this.fetchTimeline('/posts/folder/' + folderID, sortOption, includeItems, afterPostID);
    }

    private static async fetchTimeline(path: string, sortOption: string, includeItems: string, afterPostID: string) {
        path += '?';
        path += 'sort=' + (sortOption || '').toUpperCase();
        path += '&include=' + (includeItems || '').toUpperCase();
        path += '&afterPostID=' + afterPostID;

        try {
            const response = await Axios.get(path);
            return response.data;
        } catch (e) {
            return null;
        }
    }
}
