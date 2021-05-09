package com.sangupta.reread.service;

import java.util.Set;

import com.sangupta.reread.entity.DiscoveredFeed;

public interface FeedDiscoveryService {
	
	public Set<DiscoveredFeed> discoverFeeds(String url);

}
