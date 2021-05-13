package com.sangupta.reread.discover;

import java.util.Set;

import com.sangupta.jerry.http.service.HttpService;
import com.sangupta.reread.entity.DiscoveredFeed;

/**
 * Quick handler that generates RSS feed urls for Blogspot site.
 * 
 * @author sangupta
 *
 */
public class BlogspotDiscoveryHandler implements FeedDiscoveryHandler {
	
	protected HttpService httpService;

	@Override
	public boolean canHandleDiscovery(String url, String host, String path) {
		if(host.contains(".blogspot.")) {
			return true;
		}
		
		return false;
	}

	@Override
	public Set<DiscoveredFeed> discoverFeed(String url, String host, String path) {
		DiscoveredFeed rss = new DiscoveredFeed("http://" + host + "/feeds/posts/default?alt=rss", host, "rss"); 
		DiscoveredFeed atom = new DiscoveredFeed("http://" + host + "/feeds/posts/default", host, "atom");
		return Set.of(rss, atom);
	}
	
	@Override
	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

}
