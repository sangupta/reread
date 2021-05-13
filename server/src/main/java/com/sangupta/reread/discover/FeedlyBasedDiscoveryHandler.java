package com.sangupta.reread.discover;

import java.util.HashSet;
import java.util.Set;

import com.sangupta.jerry.http.service.HttpService;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.GsonUtils;
import com.sangupta.jerry.util.UriUtils;
import com.sangupta.reread.entity.DiscoveredFeed;

/**
 * Discovery handler that uses Feedly service for discovery.
 * 
 * @author sangupta
 *
 */
public class FeedlyBasedDiscoveryHandler implements FeedDiscoveryHandler {
	
	protected HttpService httpService;

	@Override
	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

	@Override
	public boolean canHandleDiscovery(String url, String host, String path) {
		return true;
	}

	@Override
	public Set<DiscoveredFeed> discoverFeed(String url, String host, String path) {
		String urlNoScheme = UriUtils.extractHost(url) + "/" + UriUtils.removeSchemeAndDomain(url);
		String feedlyUrl = "https://cloud.feedly.com/v3/search/feeds/?query=" + UriUtils.encodeURIComponent(urlNoScheme);
		String json = this.httpService.getTextResponse(feedlyUrl);
		if(AssertUtils.isEmpty(json)) {
			return null;
		}
		
		FeedlyResults feedlyResults = GsonUtils.getGson().fromJson(json, FeedlyResults.class);
		if(feedlyResults == null || AssertUtils.isEmpty(feedlyResults.results)) {
			return null;
		}
		
		Set<DiscoveredFeed> set = new HashSet<>();
		for(FeedlyResult feedlyResult : feedlyResults.results) {
			String feedUrl = feedlyResult.id;
			if(feedUrl.startsWith("feed/")) {
				feedUrl = feedUrl.substring(5);
			}
			
			String title = feedlyResult.title;
			if(AssertUtils.isEmpty(title)) {
				title = feedlyResult.websiteTitle;
			}
			
			DiscoveredFeed df = new DiscoveredFeed(feedUrl, title, "rss");
			df.iconUrl = feedlyResult.iconUrl;
			df.siteUrl = feedlyResult.website;
			set.add(df);
		}
		
		return set;
	}

	private static class FeedlyResults {
		FeedlyResult[] results;
	}
	
	private static class FeedlyResult {
		public String websiteTitle;
		public String id;
		public String iconUrl;
		public String website;
		public String title;
	}
	
}

