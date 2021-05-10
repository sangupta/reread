import Axios from 'axios';

export default class OpmlApi {

    static async importOpml(file:any) {
        console.log(typeof file);
        const response = await Axios.post('/opml/import', file);
        return response.data;
    }

}