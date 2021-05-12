package com.sangupta.reread.service;

import com.sangupta.reread.entity.ParsedFeed;

public interface FeedParsingService {

	public ParsedFeed parseFeedFromUrl(String feedID, String url, String latestPostID);

}
