export interface DiscoveredFeed {
    feedUrl: string;
    iconUrl: string;
    siteUrl: string;
    rel: string;
    title: string;
    type: string;
}

export interface Feed {
    masterFeedID: string;
    title: string;
    website?: string;
    iconUrl?: string;
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

export interface PostImage {
    url: string;
    width: number;
    height: number;
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
    image: PostImage;
}

export interface OpmlFeed {
    title: string;
    type: string;
    xmlUrl: string;
    htmlUrl: string;
    children: Array<OpmlFeed>
}

export interface ChartData {
    time: number;
    value: number;
}