package com.sangupta.reread.service;

import com.sangupta.reread.core.DataStoreService;
import com.sangupta.reread.entity.FeedList;

/**
 * Service to persist {@link FeedList} in data store.
 * 
 * @author sangupta
 *
 */
public interface FeedListService extends DataStoreService<FeedList> {

	/**
	 * Return an existing or a new {@link FeedList} for the given user.
	 * 
	 * @param userID
	 * @return
	 */
	public FeedList getOrCreate(String userID);

}
