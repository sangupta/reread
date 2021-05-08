package com.sangupta.reread.service;

import com.sangupta.reread.entity.FeedList;

public interface FeedSubscriptionService {

	public FeedList subscribe(String url);

	public FeedList unsubscribe(String feedID);

}
