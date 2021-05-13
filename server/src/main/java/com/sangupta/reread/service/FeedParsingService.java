package com.sangupta.reread.service;

import com.sangupta.reread.entity.MasterFeed;
import com.sangupta.reread.entity.ParsedFeed;

/**
 * Service to allow for parsing for RSS feeds.
 * 
 * @author sangupta
 *
 */
public interface FeedParsingService {

	/**
	 * Fetch the feed contents from given URL and parse the feed contents into
	 * a {@link ParsedFeed} object.
	 * 
	 * @param feedID the {@link MasterFeed} based ID
	 * 
	 * @param url the URL for the RSS XML file to load
	 * 
	 * @param latestPostID the ID of the most recently crawled post, if any
	 * 
	 * @return
	 */
	public ParsedFeed parseFeedFromUrl(String feedID, String url, String latestPostID);

}
