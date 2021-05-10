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

export interface FeedList {
    feeds: Array<Feed>;
    folders: Array<Folder>;
    userID: string;
}

export interface Author {
    name: string;
    uri: string;
}

export interface Post {
    baseUrl: string;
    content: string;
    feedPostID: string;
    link: string;
    masterFeedID: string;
    snippet: string;
    title: string;
    uniqueID: string;
    updated: number;
    readOn: number;
    starredOn: number;
    bookmarkedOn: number;
    author: Author;
}