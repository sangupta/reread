package com.sangupta.reread.service;

import com.sangupta.reread.entity.FeedList;
import com.sangupta.reread.entity.MasterFeed;

/**
 * Service that allows a user to subscribe to feeds.
 * 
 * @author sangupta
 *
 */
public interface FeedSubscriptionService {

	/**
	 * Subscribe the user to given {@link MasterFeed}.
	 * 
	 * @param mf
	 * @return
	 */
	public MasterFeed subscribe(MasterFeed mf, String folder);

	/**
	 * Unsusbcribe the user from the given {@link MasterFeed#feedID}
	 * 
	 * @param feedID
	 * @return
	 */
	public FeedList unsubscribe(String feedID);

}
