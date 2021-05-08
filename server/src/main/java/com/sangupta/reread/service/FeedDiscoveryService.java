package com.sangupta.reread.service;

import java.util.Set;

import com.sangupta.reread.entity.Feed;

public interface FeedDiscoveryService {
	
	public Set<Feed> discoverFeeds(String url);

}
