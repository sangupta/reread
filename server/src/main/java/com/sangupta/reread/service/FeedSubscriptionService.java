package com.sangupta.reread.service;

import com.sangupta.reread.entity.FeedList;
import com.sangupta.reread.entity.MasterFeed;

public interface FeedSubscriptionService {

	public MasterFeed subscribe(String url);

	public FeedList unsubscribe(String feedID);

}
