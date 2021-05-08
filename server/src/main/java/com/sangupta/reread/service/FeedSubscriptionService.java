package com.sangupta.reread.service;

public interface FeedSubscriptionService {

	public boolean subscribe(String url);

	public boolean unsubscribe(String feedID);

}
