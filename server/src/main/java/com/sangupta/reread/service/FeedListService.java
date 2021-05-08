package com.sangupta.reread.service;

import com.sangupta.reread.core.DataStoreService;
import com.sangupta.reread.entity.FeedList;

public interface FeedListService extends DataStoreService<FeedList> {

	public FeedList getOrCreate(String userID);

}
