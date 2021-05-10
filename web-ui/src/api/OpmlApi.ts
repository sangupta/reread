import Axios from 'axios';
import { OpmlFeed } from './Model';

export default class OpmlApi {

    static async importOpml(file: any, confirm: boolean = false): Promise<Array<OpmlFeed>> {
        const response = await Axios.post('/opml/import?confirm=' + confirm, file, {
            headers: {
                'Content-Type': 'application/octet-stream'
            }
        });
        return response.data;
    }

}
