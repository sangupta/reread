import Axios from 'axios';
import {Feed} from './Model';

export default class FeedApi {

    static async discoverFeed(url: string):Promise<Array<Feed>> {
        const response = await Axios.post('/feeds/discover', {
            url: url
        });
        return response.data;
    }

}
