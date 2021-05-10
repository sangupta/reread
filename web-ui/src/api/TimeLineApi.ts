import Axios from 'axios';

export default class TimeLineApi {

    static async getTimeLine(sort?: string, include?: string, lastPostID?: string) {
        return this.fetchTimeline('/posts/all', sort, include, lastPostID);
    }

    static async getStarsTimeLine(sort?: string, include?: string, lastPostID?: string) {
        return this.fetchTimeline('/posts/stars', sort, include, lastPostID);
    }

    static async getBookmarksTimeLine(sort?: string, include?: string, lastPostID?: string) {
        return this.fetchTimeline('/posts/bookmarks', sort, include, lastPostID);
    }

    static async getFeedTimeLine(feedID: string, sort?: string, include?: string, lastPostID?: string) {
        return this.fetchTimeline('/posts/feed/' + feedID, sort, include, lastPostID);
    }

    static async getFolderTimeLine(folderID: string, sort?: string, include?: string, lastPostID?: string) {
        return this.fetchTimeline('/posts/folder/' + folderID, sort, include, lastPostID);
    }

    private static async fetchTimeline(path: string, sort: string = '', include: string = '', lastPostID: string = '') {
        path = path + '?';
        if (sort) {
            path += '&sort=' + sort.toUpperCase();
        }
        if (include) {
            path += '&include=' + include.toUpperCase();
        }
        if (lastPostID) {
            path += '&lastPostID=' + lastPostID;
        }

        try {
            const response = await Axios.get(path);
            return response.data;
        } catch (e) {
            return null;
        }
    }
}
