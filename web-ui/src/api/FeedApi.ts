import Axios from 'axios';
import { DiscoveredFeed, FeedList } from './Model';

export default class FeedApi {

    private static FEED_LIST: FeedList = [];

    static async getFeedList() {
        const response = await Axios.get('/feeds/me');
        const list: FeedList = response.data;
        FeedApi.FEED_LIST = list;
        return list;
    }

    static async getFeedCrawlDetails(feedID: string) {
        const response = await Axios.get('/details/feed/' + feedID);
        return response.data;
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

    static async subscribeFeed(feed: DiscoveredFeed) {
        const response = await Axios.post('/feeds/subscribe', {
            url: feed.feedUrl,
            site: feed.siteUrl,
            iconUrl: feed.iconUrl,
            title: feed.title
        });
        return response.data;
    }

    static async refreshFeed(feedID: string) {
        const response = await Axios.get('/refresh/feed/' + feedID);
        return response.data;
    }

    static async refreshFolder(folderID: string) {
        const response = await Axios.get('/refresh/folder/' + folderID);
        return response.data;
    }
    
}
