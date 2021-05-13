package com.sangupta.reread.discover;

import java.util.Set;

import com.sangupta.jerry.http.service.HttpService;
import com.sangupta.reread.entity.DiscoveredFeed;

/**
 * Contract for a handler that allows discovering feed for a given URL.
 * 
 * @author sangupta
 *
 */
public interface FeedDiscoveryHandler {
	
	/**
	 * Set {@link HttpService} to use.
	 * 
	 * @param httpService
	 */
	public void setHttpService(HttpService httpService);

	/**
	 * Can this handler work on the provided URL.
	 * 
	 * @param url
	 * @param host
	 * @param path
	 * @return
	 */
	public boolean canHandleDiscovery(String url, String host, String path);

	/**
	 * If it can handle, discover the feeds.
	 * 
	 * @param url
	 * @param host
	 * @param path
	 * @return
	 */
	public Set<DiscoveredFeed> discoverFeed(final String url, final String host, final String path);
	
}
