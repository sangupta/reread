import Axios from 'axios';
import { DiscoveredFeed } from './Model';

export default class FeedApi {

    static async getFeedList() {
        const response = await Axios.get('/feeds/me');
        return response.data;
    }

    static async discoverFeed(url: string): Promise<Array<DiscoveredFeed>> {
        const response = await Axios.post('/feeds/discover', {
            url: url
        });
        return response.data;
    }

    static async subscribeFeed(feed: DiscoveredFeed) {
        const response = await Axios.post('/feeds/subscribe', {
            url: feed.url
        });
    }
}
