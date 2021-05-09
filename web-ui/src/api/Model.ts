export interface DiscoveredFeed {
    url: string;
    rel: string;
    title: string;
    type: string;
}

export interface Feed {
    masterFeedID: string;
    title: string;
}

export interface Folder {
    folderID: string;
    title: string;
    childFeeds: Array<Feed>;
}
