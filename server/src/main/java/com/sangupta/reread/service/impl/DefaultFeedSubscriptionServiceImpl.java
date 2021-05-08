package com.sangupta.reread.service.impl;

import org.springframework.stereotype.Service;

import com.sangupta.reread.service.FeedSubscriptionService;

@Service
public class DefaultFeedSubscriptionServiceImpl implements FeedSubscriptionService {

	@Override
	public boolean subscribe(String url) {
		return false;
	}

	@Override
	public boolean unsubscribe(String feedID) {
		return false;
	}

}
