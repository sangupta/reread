package com.sangupta.reread.service;

import java.util.Set;

import com.sangupta.reread.entity.DiscoveredFeed;

/**
 * Service contract for implementations that allow discovering
 * of feeds.
 * 
 * @author sangupta
 *
 */
public interface FeedDiscoveryService {
	
	/**
	 * For given website URL, return a {@link Set} of {@link DiscoveredFeed}s.
	 *
	 * @param url
	 * @return
	 */
	public Set<DiscoveredFeed> discoverFeeds(String url);

}
