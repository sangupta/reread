package com.sangupta.reread.service;

import java.util.List;

import com.sangupta.reread.entity.MasterFeed;

/**
 * Service that allows refreshing of feeds.
 * 
 * @author sangupta
 *
 */
public interface FeedRefreshService {

	/**
	 * Refresh a feed using the {@link MasterFeed#feedID}
	 * 
	 * @param masterFeedID
	 * @return
	 */
	public boolean refreshFeed(String masterFeedID);
	
	/**
	 * Refresh the given {@link MasterFeed}.
	 * 
	 * @param feed
	 * @return
	 */
	public boolean refreshFeed(MasterFeed feed);
	
	/**
	 * Refresh the given {@link List} of {@link MasterFeed}s.
	 * 
	 * @param feeds
	 * @return
	 */
	public boolean refreshFeeds(List<MasterFeed> feeds);

}
