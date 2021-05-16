import Axios from 'axios';
import { DiscoveredFeed, FeedList } from './Model';

export default class FeedApi {

    private static FEED_LIST: FeedList;

    static async getFeedList(): Promise<FeedList> {
        const response = await Axios.get('/feeds/me');
        const list: FeedList = response.data;
        FeedApi.FEED_LIST = list;
        return list;
    }

    static getFeedDetails(feedID: string) {
        const list = FeedApi.FEED_LIST;
        let found = list.feeds.find(item => item.masterFeedID === feedID);
        if (!found) {
            list.folders.forEach(folder => {
                found = folder.childFeeds.find(feed => feed.masterFeedID === feedID);
            });
        }

        return found;
    }

    static async discoverFeed(url: string): Promise<Array<DiscoveredFeed>> {
        const response = await Axios.post('/feeds/discover', {
            url: url
        });
        return response.data;
    }

    static async subscribeFeed(feed: DiscoveredFeed, folder: string) {
        const response = await Axios.post('/feeds/subscribe', {
            url: feed.feedUrl,
            site: feed.siteUrl,
            iconUrl: feed.iconUrl,
            title: feed.title,
            folder: folder
        });
        return response.data;
    }

    static async unsubscribeFeed(feedID: string): Promise<FeedList> {
        const response = await Axios.post('/feeds/unsubscribe', {
            feedID: feedID
        });
        return response.data;
    }

    static async refreshFeed(feedID: string): Promise<String> {
        const response = await Axios.get('/refresh/feed/' + feedID);
        return response.data;
    }

    static async refreshFolder(folderID: string): Promise<String> {
        const response = await Axios.get('/refresh/folder/' + folderID);
        return response.data;
    }

    static async refreshAll(): Promise<String> {
        const response = await Axios.get('/refresh/all');
        return response.data;
    }

    static async getFeedCrawlDetails(feedID: string) {
        const response = await Axios.get('/details/feed/' + feedID);
        return response.data;
    }

    static async getFeedChart(feedID: string) {
        const response = await Axios.get('/details/chart/feed/' + feedID);
        return response.data;
    }

    static async getActivityChart(activity: string, type: string, interval: string): Promise<Array<any>> {
        const response = await Axios.get('/details/chart/activity/' + activity.toUpperCase() + '?metrics=' + type + '&interval=' + interval);
        return response.data;
    }

    static async createFolder(name: string) {
        const response = await Axios.post('/feeds/folder?name=' + encodeURIComponent(name));
        return response.data;
    }

    static async exportOpml() {
        
    }
}
