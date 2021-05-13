package com.sangupta.reread.service;

import com.sangupta.reread.entity.MasterFeed;

/**
 * Service contract for implementation that allow crawling a feed.
 * 
 * @author sangupta
 *
 */
public interface FeedCrawlerService {
	
	/**
	 * Crawl, persist, index and update the timeline for the
	 * given {@link MasterFeed} based on its ID.
	 * 
	 * @param masterFeedID
	 */
	public void crawlFeed(String masterFeedID);

}
