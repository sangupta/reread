package com.sangupta.reread.discover;

import java.util.Set;

import com.sangupta.jerry.http.service.HttpService;
import com.sangupta.reread.entity.DiscoveredFeed;

public interface FeedDiscoveryHandler {
	
	public void setHttpService(HttpService httpService);

	public boolean canHandleDiscovery(String url, String host, String path);

	public Set<DiscoveredFeed> discoverFeed(final String url, final String host, final String path);
	
}
